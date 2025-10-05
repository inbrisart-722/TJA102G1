package com.eventra.exhibition.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventra.exhibition.backend.controller.dto.ExhibitionReviewReqDTO;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibition_review_log.model.ExhibitionReviewLogRepository;
import com.eventra.exhibition_review_log.model.ExhibitionReviewLogVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/platform/exhibition")
public class ExhibitionModerationController {

    private final ExhibitionRepository exhibitionRepo;
    private final ExhibitionReviewLogRepository reviewLogRepo;
    private final ExhibitionService exhibitionService;

    public ExhibitionModerationController(ExhibitionRepository exhibitionRepo,
                                          ExhibitionReviewLogRepository reviewLogRepo,ExhibitionService exhibitionService) {
        this.exhibitionRepo = exhibitionRepo;
        this.reviewLogRepo = reviewLogRepo;
        this.exhibitionService = exhibitionService;
    }

    /** 按「審核成功(4) / 審核失敗(3)」 */
    @PostMapping("/review/status")
    @Transactional
    public ResponseEntity<?> review(@RequestBody ExhibitionReviewReqDTO req) {
        if (req.getExhibitionId() == null || req.getStatusId() == null) {
            return ResponseEntity.badRequest().body("exhibitionId / statusId 不可為空");
        }
        int status = req.getStatusId();
        if (status != 3 && status != 4) {
            return ResponseEntity.badRequest().body("statusId 僅接受 3 或 4");
        }
        if (status == 3) { // 失敗時必須有原因
            String reason = req.getRejectReason() == null ? "" : req.getRejectReason().trim();
            if (reason.isEmpty()) return ResponseEntity.badRequest().body("審核失敗時需提供 rejectReason");
        }

        ExhibitionVO e = exhibitionRepo.findById(req.getExhibitionId()).orElseThrow();
        e.setExhibitionStatusId(status);        // 回寫展覽狀態
        exhibitionRepo.save(e);

        // 寫入審核紀錄：成功=reason=null；失敗=寫入理由
        ExhibitionReviewLogVO log = new ExhibitionReviewLogVO();
        log.setExhibitionId(e.getExhibitionId());
        log.setRejectReason(status == 3 ? req.getRejectReason().trim() : null);
        reviewLogRepo.save(log);

        return ResponseEntity.ok().build();
    }

    /** 按「結束展覽(5)」 */
    @Controller
    @RequestMapping("/platform/exhibition")
    public class PlatformExhibitionViewController {

        private final ExhibitionService exhibitionService;

        public PlatformExhibitionViewController(ExhibitionService exhibitionService) {
            this.exhibitionService = exhibitionService;
        }

        @PostMapping("/end")
        @Transactional
        public String endExhibition(@RequestParam Integer exhibitionId,
                                    RedirectAttributes ra) {
            exhibitionService.updateStatus(exhibitionId, 5); // 這裡會呼叫你那段 Service，沒問題
            ra.addFlashAttribute("msg", "展覽已結束");
            return "redirect:/platform/exhibition";
        }
    }

    
    
    @PostMapping("/platform/exhibition/review/status")
    @Transactional
    public String reviewFromForm(@Valid ExhibitionReviewReqDTO dto,
                                 BindingResult br,
                                 RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", "資料驗證失敗");
            return "redirect:/platform/exhibition_detail?exhibitionId=" + dto.getExhibitionId();
        }
        exhibitionService.reviewAndSave(dto);
        ra.addFlashAttribute("msg", "已儲存");
        return "redirect:/platform/exhibition_detail?exhibitionId=" + dto.getExhibitionId();
    }

    
    
    
}
