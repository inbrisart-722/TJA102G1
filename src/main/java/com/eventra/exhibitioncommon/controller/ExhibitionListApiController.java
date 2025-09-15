package com.eventra.exhibitioncommon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;
import com.eventra.exhibitioncommon.model.ExhibitionListService;

@RestController // 處理API
@RequestMapping("/api/exhibitions")
public class ExhibitionListApiController {
	/* 集中處理html的展覽清單 */
	
    /* 待處理: 
     * 1. 熱門展覽清單頁/最新展覽清單頁 - 評價平均星數
     * 2. 熱門展覽清單頁/最新展覽清單頁 - 分頁功能
     */
	
	@Autowired
    private ExhibitionListService service;
	
	static final int  TOPN = 5;
	static final int  DAYS = 14;
	
	/* ===== 首頁 ===== */
	
    // 熱門展覽 (前 topN 筆, 近 days 天)
    @GetMapping("/popular/topN")
    public List<ExhibitionListDTO> getTopNPopular() {
        return service.getTopNPopularExhibitions(TOPN, DAYS);
    }

    // 最新展覽 (前 topN 筆)
    @GetMapping("/latest/topN")
    public List<ExhibitionListDTO> getTopNLatest() {
        return service.getTopNLatestExhibitions(TOPN);
    }
    
    /* ===== 熱門展覽清單頁 ===== */
    @GetMapping("/popular")
    public List<ExhibitionListDTO> getPopular() {
        return service.getPopularExhibitions(DAYS);
    }
    
    
    /* ===== 最新展覽清單頁 ===== */
    @GetMapping("/latest")
    public List<ExhibitionListDTO> getLatest() {
        return service.getLatestExhibitions();
    }
	

}
