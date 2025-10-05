package com;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionReviewPageDTO;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitionreviewlog.model.ExhibitionReviewLogVO;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.exhibitor_review_log.model.ExhibitorReviewLogRepository;
import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Controller
@RequestMapping("/")
public class PlatformIndexController {

	private final MemberRepository MEMBER_REPO;
	private final ExhibitorRepository EXHIBITOR_REPO;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final ExhibitionServiceImpl EXHIBITION_SVC;
	private final ExhibitorReviewLogRepository EXHIBITIONREVIEW_REPO;

	public PlatformIndexController(MemberRepository memberRepo,ExhibitorRepository exhibitorRepo,ExhibitionRepository exhibitionRepo, ExhibitionServiceImpl exhibitionSvc, ExhibitorReviewLogRepository exhibitionreviewRepo) {
		this.MEMBER_REPO = memberRepo;
		this.EXHIBITOR_REPO = exhibitorRepo;
		this.EXHIBITION_REPO = exhibitionRepo;
		this.EXHIBITION_SVC = exhibitionSvc;
		this.EXHIBITIONREVIEW_REPO = exhibitionreviewRepo;
	}

	@GetMapping("/platform/comment")
	public String commenT() {
		return "platform/comment";
	}

	
	
	
	
	
	
	@GetMapping("/platform/exhibition")
	public String exhibition(Model model) {
//		ExhibitionVO exhibition = EXHIBITION_REPO.findById(5).orElseThrow();

//		model.addAttribute("EXHIBITION_REPO", exhibition);
		List<ExhibitionReviewPageDTO> exhibitionList = EXHIBITION_SVC.getExhibitionsForReviewPage();
		model.addAttribute("exhibitionList", exhibitionList);
		return "platform/exhibition";

	}

	@PostMapping("/platform/exhibition_detail")
	public String exhibitionDetail(@RequestParam("exhibitionId") Integer exhibitionId,Model model) {
		
		
		
		
		ExhibitionVO exhibition = EXHIBITION_REPO.findById(exhibitionId) .orElseThrow();
		
		model.addAttribute("exhibitionVO", exhibition);
		
		
		
		
		return "platform/exhibition_detail";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/platform/exhibitor")
	public String exhibitor(Model model) {
		List<ExhibitorVO> exhibitorList = EXHIBITOR_REPO.findByReviewStatusIdIn(java.util.List.of(1, 3));
		model.addAttribute("exhibitorList", exhibitorList);
		return "platform/exhibitor";
	}

	@PostMapping("/platform/exhibitor_detail")
	public String exhibitorDetail(@RequestParam("exhibitorId") Integer exhibitorId, Model model) {
		ExhibitorVO exhibitor = EXHIBITOR_REPO.findById(exhibitorId).orElseThrow();
		model.addAttribute("exhibitorVO", exhibitor);
		return "platform/exhibitor_detail";
	}

	
	
	
	
	
	
	
	// =========================================
	@PostMapping("/platform/exhibitor/review")
	@Transactional
	public String reviewExhibitor(@RequestParam("exhibitorId") Integer exhibitorId,

			// 二選一：字串或數字（兩個都送也行，數字優先）
			@RequestParam(value = "status", required = false) String status, // APPROVED / REJECTED
			@RequestParam(value = "reviewStatusId", required = false) Integer reviewStatusId, // 2 / 3

			// 其他可回存欄位（可為 null）
			@RequestParam(required = false) String companyName, @RequestParam(required = false) String businessIdNumber,
			@RequestParam(required = false) String contactName,
			@RequestParam(required = false) String exhibitorRegistrationName,
			@RequestParam(required = false) String contactPhone, @RequestParam(required = false) String companyAddress,
			@RequestParam(required = false) String email, @RequestParam(required = false) String rejectReason,
			RedirectAttributes ra) {

		// 1) 取出實體
		ExhibitorVO exhibitorVO = EXHIBITOR_REPO.findById(exhibitorId).orElseThrow();

		// 2) 解析審核狀態（數字優先，其次字串）
		Integer resolved = reviewStatusId;
		if (resolved == null && status != null) {
			resolved = switch (status.trim().toUpperCase()) {
			case "APPROVED" -> 2;
			case "REJECTED" -> 3;
			default -> 1; // 其他當 PENDING
			};
		}
		if (resolved == null)
			resolved = 1; // 預設 PENDING
		exhibitorVO.setReviewStatusId(resolved);

		// 3) 可回存欄位（有送才覆蓋）
		if (companyName != null)
			exhibitorVO.setCompanyName(companyName.trim());
		if (businessIdNumber != null)
			exhibitorVO.setBusinessIdNumber(businessIdNumber.trim());
		if (exhibitorRegistrationName != null)
			exhibitorVO.setExhibitorRegistrationName(exhibitorRegistrationName.trim());
		if (contactName != null)
			exhibitorVO.setContactName(contactName.trim());
		if (contactPhone != null)
			exhibitorVO.setContactPhone(contactPhone.trim());
		if (companyAddress != null)
			exhibitorVO.setCompanyAddress(companyAddress.trim());
		if (email != null)
			exhibitorVO.setEmail(email.trim());

		// 若 DB 有 reject_reason 欄位且 VO 有對應屬性，再打開：
		// if (resolved == 3) vo.setRejectReason(blankToNull(rejectReason));
		// else vo.setRejectReason(null);

		// 4) 立刻送出 UPDATE（避免延後 flush）
		EXHIBITOR_REPO.saveAndFlush(exhibitorVO);

		ra.addFlashAttribute("msg", "已更新審核狀態（ID=" + exhibitorId + "）為 " + resolved);
		return "redirect:/platform/exhibitor";
	}
	// ======================================================================

	@GetMapping("/platform/index")
	public String indeX() {
		return "platform/index";
	}

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}

	@GetMapping("/platform/member")
	public String member(Model model) {
//		MemberVO member = MEMBER_REPO.findById(5).orElseThrow();
		List<MemberVO> memberList = MEMBER_REPO.findAll();
//		model.addAttribute("memberVO", member);
		model.addAttribute("memberList", memberList);
		return "platform/member";
	}

	@PostMapping("/platform/member_edit")
	public String memberEdit(@RequestParam("memberId") Integer memberId, Model model) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		model.addAttribute("memberVO", member);
		return "platform/member_edit";
	}

	@GetMapping("/platform/platform_edit")
	public String platformEdit() {
		return "platform/platform_edit";
	}

	@GetMapping("/platform/report")
	public String report() {
		return "platform/report";
	}

}