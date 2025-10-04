package com.eventra.exhibitioncommon.util;

import java.sql.Timestamp;
import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;

public class ExhibitionListUtil {

    // 1. 首頁 (熱門/最新) - 基本欄位
	public static ExhibitionListDTO toHomepageDTO(Object[] r, boolean hasTotalViews) {
        ExhibitionListDTO dto = new ExhibitionListDTO();
        dto.setExhibitionId((Integer) r[0]);
        dto.setExhibitionName((String) r[1]);
        dto.setPhotoPortrait((String) r[2]);
        if (hasTotalViews) {
            dto.setTotalViews(r[3] != null ? ((Number) r[3]).intValue() : 0);
        }
        return dto;
    }

    // 2. 熱門展覽頁 (含 totalViews)
	public static ExhibitionListDTO toPopularDTO(Object[] r) {
        ExhibitionListDTO dto = new ExhibitionListDTO();
        dto.setExhibitionId((Integer) r[0]);
        dto.setExhibitionName((String) r[1]);
        dto.setPhotoPortrait((String) r[2]);
        dto.setMinPrice(r[3] != null ? ((Number) r[3]).intValue() : null);
        dto.setMaxPrice(r[4] != null ? ((Number) r[4]).intValue() : null);
        dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null);
        dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null);
        dto.setLocation((String) r[7]);
        dto.setAverageRatingScore(r[8] != null ? ((Number) r[8]).doubleValue() : 0.0);
        dto.setRatingCount(r[9] != null ? ((Number) r[9]).intValue() : 0);
        dto.setTotalViews(r[10] != null ? ((Number) r[10]).intValue() : 0);
        return dto;
    }

    // 3. 最新展覽頁 / 搜尋結果頁 / 展商主頁 (不含 totalViews)
	public static ExhibitionListDTO toListDTO(Object[] r) {
        ExhibitionListDTO dto = new ExhibitionListDTO();
        dto.setExhibitionId((Integer) r[0]);
        dto.setExhibitionName((String) r[1]);
        dto.setPhotoPortrait((String) r[2]);
        dto.setMinPrice(r[3] != null ? ((Number) r[3]).intValue() : null);
        dto.setMaxPrice(r[4] != null ? ((Number) r[4]).intValue() : null);
        dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null);
        dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null);
        dto.setLocation((String) r[7]);
        dto.setAverageRatingScore(r[8] != null ? ((Number) r[8]).doubleValue() : 0.0);
        dto.setRatingCount(r[9] != null ? ((Number) r[9]).intValue() : 0);
        return dto;
    }
	
}
