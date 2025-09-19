package com.eventra.exhibitioncommon.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;
import com.eventra.exhibitioncommon.util.ExhibitionUtilCompositeQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ExhibitionListService {

	@Autowired
	private ExhibitionListRepository repository;

	@PersistenceContext
    private EntityManager entityManager;
	
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
            dto.setExhibitionId((Integer) r[0]);                 							// exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                							// exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                							// photo_landscape[2]
            dto.setMinPrice(((Number) r[3]).intValue());         							// minPrice[3]
            dto.setMaxPrice(((Number) r[4]).intValue());         							// maxPrice[4]
            dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null); 	// start_time[5]
            dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null); 	// end_time[6]
            dto.setLocation((String) r[7]);                      							// location[7]
            dto.setRatingCount(((Number) r[8]).intValue());      							// ratingCount[8]
            dto.setTotalViews(((Number) r[9]).intValue());       							// totalViews[9]
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
            dto.setExhibitionId((Integer) r[0]);                 							// exhibition_id[0]
            dto.setExhibitionName((String) r[1]);                							// exhibition_name[1]
            dto.setPhotoLandscape((String) r[2]);                							// photo_landscape[2]
            dto.setMinPrice(((Number) r[3]).intValue());         							// minPrice[3]
            dto.setMaxPrice(((Number) r[4]).intValue());         							// maxPrice[4]
            dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null); 	// start_time[5]
            dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null); 	// end_time[6]
            dto.setLocation((String) r[7]);                      							// location[7]
            dto.setRatingCount(((Number) r[8]).intValue());      							// ratingCount[8]
            result.add(dto);
        }
        return result;
    }
    
 // ========================================================================================================================
    
    /* ===== 搜尋展覽清單頁 ===== */
    
    /* 複合查詢 */
    public List<ExhibitionListDTO> searchExhibitions(Map<String, String[]> criteria) {
        return ExhibitionUtilCompositeQuery.getAllC(criteria, entityManager);
    }
        
    /* 複合查詢 - 測試/debug用 */
    public List<ExhibitionListDTO> searchExhibitionsByNameAndDateRange(
            String keyword, String startDate, String endDate, List<String> regions) {

        StringBuilder sql = new StringBuilder(
            "SELECT e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
            "MIN(ett.price) AS minPrice, " +
            "MAX(ett.price) AS maxPrice, " +
            "e.start_time, e.end_time, e.location, e.total_rating_count " +
            "FROM exhibition e " +
            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
            "WHERE e.exhibition_status_id IN (3,4) " +
            (keyword != null ? "AND e.exhibition_name LIKE :keyword " : "") +
            "AND e.start_time <= :endDate " +
            "AND e.end_time >= :startDate "
        );

        // 多地區
        if (regions != null && !regions.isEmpty()) {
            sql.append("AND (");
            for (int i = 0; i < regions.size(); i++) {
                if (i > 0) sql.append(" OR ");
                sql.append("e.location LIKE :region" + i);
            }
            sql.append(") ");
        }

        sql.append("GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
                   "e.start_time, e.end_time, e.location, e.total_rating_count " +
                   "ORDER BY e.exhibition_id DESC LIMIT 50");

        Query query = entityManager.createNativeQuery(sql.toString());

        if (keyword != null) query.setParameter("keyword", keyword);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        if (regions != null && !regions.isEmpty()) {
            for (int i = 0; i < regions.size(); i++) {
                query.setParameter("region" + i, "%" + regions.get(i) + "%");
            }
        }

        List<Object[]> list = query.getResultList();
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);
            dto.setExhibitionName((String) r[1]);
            dto.setPhotoLandscape((String) r[2]);
            dto.setMinPrice(((Number) r[3]).intValue());
            dto.setMaxPrice(((Number) r[4]).intValue());
            dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null);
            dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null);
            dto.setLocation((String) r[7]);
            dto.setRatingCount(((Number) r[8]).intValue());
            result.add(dto);
        }
        return result;
    }


}
