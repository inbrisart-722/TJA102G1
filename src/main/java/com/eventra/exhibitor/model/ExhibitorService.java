package com.eventra.exhibitor.model;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExhibitorService {

	private  ExhibitorRepository exhibitorRepo;

    public ExhibitorVO getExhibitorById(Integer id) {
        return exhibitorRepo.findById(id).orElse(null);
    }
    
    


    @Transactional
    public ExhibitorVO saveOrUpdate(ExhibitorSaveReq req) {
        ExhibitorVO exhibitorVO = (req.getExhibitorId() == null)
                ? new ExhibitorVO()
                : exhibitorRepo.findById(req.getExhibitorId())
                      .orElseThrow(() -> new IllegalArgumentException("Exhibitor not found: " + req.getExhibitorId()));

        // === 將 DTO 值塞回 Entity ===
        exhibitorVO.setCompanyName(nz(req.getCompanyName()));
        exhibitorVO.setBusinessIdNumber(nz(req.getBusinessIdNumber()));
        exhibitorVO.setEmail(nz(req.getEmail()));
        exhibitorVO.setContactName(nz(req.getContactName()));
        exhibitorVO.setExhibitorRegistrationName(nz(req.getGetExhibitorRegistrationName()));
        exhibitorVO.setContactPhone(nz(req.getContactPhone()));
        exhibitorVO.setCompanyAddress(nz(req.getCompanyAddress()));

        // 審核欄位（若你要按鈕直接傳 2=成功 / 3=失敗）
        if (req.getReviewStatusId() != null) {
        	exhibitorVO.setReviewStatusId(req.getReviewStatusId());
        }
        // 這個欄位目前不在 Entity，你若要存，請在 VO 加上欄位
        // vo.setRejectReason(blankToNull(req.getRejectReason()));

        return exhibitorRepo.save(exhibitorVO); // 新增或更新都可以；更新時 JPA 會自動發 UPDATE
    }

    private static String nz(String s) { return s == null ? "" : s.trim(); }
    // 若 DB 欄位允許 NULL 而你想存 NULL 就換成：
    // private static String blankToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
    
    

