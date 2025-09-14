package com.eventra.exhibitioncommon.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;

@Service
public class ExhibitionListService {

	@Autowired
	private ExhibitionListRepository repository;

	/* ===== 首頁 ===== */
	
	/* 熱門展覽區, 查詢統計近"n"天的熱門展覽列表, 前"n"筆 */
    public List<ExhibitionListDTO> getTopNPopularExhibitions(int topN, int days) {
        List<Object[]> list = repository.findTopNPopularExhibitionsLastNDays(topN, days);
        
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);                 // exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                // exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                // photo_landscape[2]
            dto.setTotalViews(((Number) r[3]).intValue());       // totalViews[3]
            result.add(dto);
        }
        return result;
    }

	/* 最新展覽區, 查詢最新展覽列表, 前"n"筆 */
    public List<ExhibitionListDTO> getTopNLatestExhibitions(int topN) {
        List<Object[]> list = repository.findTopNLatestExhibitions(topN);
        
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);                 // exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                // exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                // photo_landscape[2]
            result.add(dto);
        }
        return result;
    }

	// ========================================================================================================================

	/* ===== 熱門展覽清單頁/最新展覽清單頁 ===== */

	/* 熱門 - 查詢統計近"n"天的熱門展覽列表 */
    public List<ExhibitionListDTO> getPopularExhibitions(int days) {
        List<Object[]> list = repository.findPopularExhibitionsLastNDays(days);
        
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);                 // exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                // exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                // photo_landscape[2]
            dto.setMinPrice(((Number) r[3]).intValue());         // minPrice[3]
            dto.setMaxPrice(((Number) r[4]).intValue());         // maxPrice[4]
            dto.setStartTime((java.sql.Timestamp) r[5]);         // start_time[5]
            dto.setEndTime((java.sql.Timestamp) r[6]);           // end_time[6]
            dto.setLocation((String) r[7]);                      // location[7]
            dto.setRatingCount(((Number) r[8]).intValue());      // ratingCount[8]
            dto.setTotalViews(((Number) r[9]).intValue());       // totalViews[9]
            result.add(dto);
        }
        return result;
    }

	/* 最新 - 查詢最新展覽列表 */
    public List<ExhibitionListDTO> getLatestExhibitions() {
        List<Object[]> list = repository.findLatestExhibitions();
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);                 // exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                // exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                // photo_landscape[2]
            dto.setMinPrice(((Number) r[3]).intValue());         // minPrice[3]
            dto.setMaxPrice(((Number) r[4]).intValue());         // maxPrice[4]
            dto.setStartTime((java.sql.Timestamp) r[5]);         // start_time[5]
            dto.setEndTime((java.sql.Timestamp) r[6]);           // end_time[6]
            dto.setLocation((String) r[7]);                      // location[7]
            dto.setRatingCount(((Number) r[8]).intValue());      // ratingCount[8]
            result.add(dto);
        }
        return result;
    }
}
