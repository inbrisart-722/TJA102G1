package com.eventra.exhibitioncommon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;
import com.eventra.exhibitioncommon.util.ExhibitionListUtil;
import com.eventra.exhibitioncommon.util.ExhibitionPagingUtil;
import com.eventra.exhibitioncommon.util.ExhibitionUtilCompositeQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ExhibitionListService {

	@Autowired
	private ExhibitionListRepository repository;

	@PersistenceContext
    private EntityManager entityManager;
	
	/* ===== 首頁 ===== */
	public List<ExhibitionListDTO> getTopNPopularExhibitions(int topN, int days) {
        List<Object[]> list = repository.findTopNPopularExhibitionsLastNDays(topN, days);
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            result.add(ExhibitionListUtil.toHomepageDTO(r, true));
        }
        return result;
    }

    public List<ExhibitionListDTO> getTopNLatestExhibitions(int topN) {
        List<Object[]> list = repository.findTopNLatestExhibitions(topN);
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            result.add(ExhibitionListUtil.toHomepageDTO(r, false));
        }
        return result;
    }

    
    /* ===== 熱門展覽清單頁 ===== */
    public Map<String, Object> getPopularExhibitionsPaged(int days, int page, int size) {
        int offset = (page - 1) * size;
        List<Object[]> list = repository.findPopularExhibitionsLastNDaysPaged(days, size, offset);
        int totalElements = repository.countPopularExhibitionsLastNDays(days);

        return ExhibitionPagingUtil.buildPagedResult(
                list, totalElements, page, size, ExhibitionListUtil::toPopularDTO
        );
    }

    /* ===== 最新展覽清單頁 ===== */
    public Map<String, Object> getLatestExhibitionsPaged(int page, int size) {
        int offset = (page - 1) * size;
        List<Object[]> list = repository.findLatestExhibitionsPaged(size, offset);
        int totalElements = repository.countLatestExhibitions();

        return ExhibitionPagingUtil.buildPagedResult(
                list, totalElements, page, size, ExhibitionListUtil::toListDTO
        );
    }

    /* ===== 搜尋展覽清單頁 (分頁, CriteriaQuery) ===== */
    public Map<String, Object> searchExhibitionsPaged(Map<String, String[]> criteria, int page, int size) {
        int offset = (page - 1) * size;

        // CriteriaQuery 查詢 + 分頁
        List<ExhibitionListDTO> list = ExhibitionUtilCompositeQuery.getAllC(criteria, entityManager, offset, size);
        int totalElements = ExhibitionUtilCompositeQuery.countAllC(criteria, entityManager);

        // 已經是 DTO，不需要 mapper
        return ExhibitionPagingUtil.buildPagedResult(list, totalElements, page, size);
    }
        
    /* 複合查詢 - 測試/debug用 */
//    public List<ExhibitionListDTO> searchExhibitionsByNameAndDateRange(
//            String keyword, String startDate, String endDate, List<String> regions) {
//
//        StringBuilder sql = new StringBuilder(
//            "SELECT e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
//            "MIN(ett.price) AS minPrice, " +
//            "MAX(ett.price) AS maxPrice, " +
//            "e.start_time, e.end_time, e.location, e.total_rating_count " +
//            "FROM exhibition e " +
//            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
//            "WHERE e.exhibition_status_id IN (3,4) " +
//            (keyword != null ? "AND e.exhibition_name LIKE :keyword " : "") +
//            "AND e.start_time <= :endDate " +
//            "AND e.end_time >= :startDate "
//        );
//
//        // 多地區
//        if (regions != null && !regions.isEmpty()) {
//            sql.append("AND (");
//            for (int i = 0; i < regions.size(); i++) {
//                if (i > 0) sql.append(" OR ");
//                sql.append("e.location LIKE :region" + i);
//            }
//            sql.append(") ");
//        }
//
//        sql.append("GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
//                   "e.start_time, e.end_time, e.location, e.total_rating_count " +
//                   "ORDER BY e.exhibition_id DESC LIMIT 50");
//
//        Query query = entityManager.createNativeQuery(sql.toString());
//
//        if (keyword != null) query.setParameter("keyword", keyword);
//        query.setParameter("startDate", startDate);
//        query.setParameter("endDate", endDate);
//
//        if (regions != null && !regions.isEmpty()) {
//            for (int i = 0; i < regions.size(); i++) {
//                query.setParameter("region" + i, "%" + regions.get(i) + "%");
//            }
//        }
//
//        List<Object[]> list = query.getResultList();
//        List<ExhibitionListDTO> result = new ArrayList<>();
//        for (Object[] r : list) {
//            ExhibitionListDTO dto = new ExhibitionListDTO();
//            dto.setExhibitionId((Integer) r[0]);
//            dto.setExhibitionName((String) r[1]);
//            dto.setPhotoLandscape((String) r[2]);
//            dto.setMinPrice(((Number) r[3]).intValue());
//            dto.setMaxPrice(((Number) r[4]).intValue());
//            dto.setStartTime(r[5] != null ? ((Timestamp) r[5]).toLocalDateTime() : null);
//            dto.setEndTime(r[6] != null ? ((Timestamp) r[6]).toLocalDateTime() : null);
//            dto.setLocation((String) r[7]);
//            dto.setAverageRatingScore(((Number) r[8]).doubleValue());
//            dto.setRatingCount(((Number) r[9]).intValue());
//            result.add(dto);
//        }
//        return result;
//    }

    /* ===== 展商主頁 ===== */
    public Map<String, Object> getExhibitionsByExhibitorPaged(Integer exhibitorId, int page, int size) {
        int offset = (page - 1) * size;
        List<Object[]> list = repository.findExhibitionsByExhibitorPaged(exhibitorId, size, offset);
        int totalElements = repository.countExhibitionsByExhibitor(exhibitorId);

        return ExhibitionPagingUtil.buildPagedResult(
                list, totalElements, page, size, ExhibitionListUtil::toListDTO
        );
    }
    
    /* ===== 首頁輪播圖 (每日隨機三個展覽) ===== */
    public List<ExhibitionListDTO> getDailyRandomThree() {
        List<Object[]> list = repository.findDailyRandomThree();
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            result.add(ExhibitionListUtil.toHomepageDTO(r, false));
        }
        return result;
    }


}
