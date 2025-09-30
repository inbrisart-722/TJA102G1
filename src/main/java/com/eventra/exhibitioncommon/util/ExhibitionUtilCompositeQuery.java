package com.eventra.exhibitioncommon.util;

import java.util.*;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;

public class ExhibitionUtilCompositeQuery {	
	// 處理前台頁面"展覽清單"的動態搜尋條件組裝
	// keyword / regions / date_range(date_from/date_to) 條件
	// 僅單純組裝 CriteriaQuery，不直接執行查詢, 查詢執行由 Service 層透過 EntityManager 呼叫完成
	// 用於前台 Header 搜尋彈窗的展覽查詢
	
	
	/* ===== 單一條件 ===== */
	
	// 關鍵字: exhibitionName LIKE %value%
	public static Predicate get_aPredicate_ForKeyword(CriteriaBuilder cb, Root<ExhibitionVO> root, String value) {
        return cb.like(root.get("exhibitionName"), "%" + value + "%");
    }

	// 日期區間：from 與 to 組合
	public static Predicate get_aPredicate_ForDateRange(CriteriaBuilder cb, Root<ExhibitionVO> root, String dateFrom, String dateTo) {
		/* 
		 * 邏輯: 
		 * 情境: 查詢 2025-09-01 至 2025-12-31 區間 的有效展覽
		 * 1. 排除結束日期在 2025-09-01 00:00:00 以前的展覽
		 * 2. 排除開始日期在 2025-12-31 23:59:59 以後的展覽
		 * */
		
		// endTime >= dateFrom 00:00:00, 手工把時分秒加上, 避免 SQL 出錯
	    Predicate greater = cb.greaterThanOrEqualTo(
	        root.get("endTime"),
	        java.sql.Timestamp.valueOf(dateFrom + " 00:00:00")
	    );

	    // startTime <= dateTo 23:59:59, 手工把時分秒加上, 避免 SQL 出錯
	    Predicate less = cb.lessThanOrEqualTo(
	        root.get("startTime"),
	        java.sql.Timestamp.valueOf(dateTo + " 23:59:59")
	    );

	    return cb.and(greater, less); // 兩條件同時成立, 時間有交集
	}

	// 多地區: 任一符合即通過
	public static Predicate get_aPredicate_ForRegions(CriteriaBuilder cb, Root<ExhibitionVO> root, String[] values) {
        List<Predicate> orList = new ArrayList<>(); // 暫存每個地區的 LIKE 條件
        
        // 逐一產生 LIKE 條件
        for (String region : values) {
            orList.add(cb.like(root.get("location"), "%" + region + "%")); // location LIKE %region%
        }
        return cb.or(orList.toArray(new Predicate[orList.size()])); // 用 OR 串接條件
    }
	
	// ========================================================================================================================
	
	/* ===== 複合條件 (查詢分頁版) ===== */
    public static List<ExhibitionListDTO> getAllC(
            Map<String, String[]> map,
            EntityManager em,
            int offset,
            int size) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<ExhibitionVO> root = cq.from(ExhibitionVO.class);
        Join<ExhibitionVO, ExhibitionTicketTypeVO> ticket = root.join("exhibitionTicketTypes", JoinType.LEFT);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(root.get("exhibitionStatusId").in(Arrays.asList(3, 4)));

        if (map.containsKey("keyword")) {
            predicateList.add(get_aPredicate_ForKeyword(cb, root, map.get("keyword")[0]));
        }
        if (map.containsKey("date_from") && map.containsKey("date_to")) {
            predicateList.add(get_aPredicate_ForDateRange(cb, root,
                    map.get("date_from")[0], map.get("date_to")[0]));
        }
        if (map.containsKey("regions")) {
            predicateList.add(get_aPredicate_ForRegions(cb, root, map.get("regions")));
        }

        cq.multiselect(
                root.get("exhibitionId"),
                root.get("exhibitionName"),
                root.get("photoLandscape"),
                cb.min(ticket.get("price")),
                cb.max(ticket.get("price")),
                root.get("startTime"),
                root.get("endTime"),
                root.get("location"),
                root.get("totalRatingScore"),
                root.get("totalRatingCount")
        )
                .where(predicateList.toArray(new Predicate[0]))
                .groupBy(root.get("exhibitionId"), root.get("exhibitionName"),
                        root.get("photoLandscape"), root.get("startTime"),
                        root.get("endTime"), root.get("location"),
                        root.get("totalRatingScore"), root.get("totalRatingCount"))
                .orderBy(cb.desc(root.get("exhibitionId")));

        TypedQuery<Object[]> query = em.createQuery(cq);
        query.setFirstResult(offset);
        query.setMaxResults(size);

        return mapResult(query.getResultList());
    }

    /* ===== 複合條件 (計算總數) ===== */
    public static int countAllC(Map<String, String[]> map, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<ExhibitionVO> root = cq.from(ExhibitionVO.class);

        cq.select(cb.countDistinct(root));

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(root.get("exhibitionStatusId").in(Arrays.asList(3, 4)));

        if (map.containsKey("keyword")) {
            predicateList.add(get_aPredicate_ForKeyword(cb, root, map.get("keyword")[0]));
        }
        if (map.containsKey("date_from") && map.containsKey("date_to")) {
            predicateList.add(get_aPredicate_ForDateRange(cb, root,
                    map.get("date_from")[0], map.get("date_to")[0]));
        }
        if (map.containsKey("regions")) {
            predicateList.add(get_aPredicate_ForRegions(cb, root, map.get("regions")));
        }

        cq.where(predicateList.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult().intValue();
    }

    /* ===== 工具: Object[] 轉 DTO ===== */
    private static List<ExhibitionListDTO> mapResult(List<Object[]> list) {
        List<ExhibitionListDTO> result = new ArrayList<>();
        for (Object[] r : list) {
            ExhibitionListDTO dto = new ExhibitionListDTO();
            dto.setExhibitionId((Integer) r[0]);
            dto.setExhibitionName((String) r[1]);
            dto.setPhotoLandscape((String) r[2]);
            dto.setMinPrice(r[3] != null ? ((Number) r[3]).intValue() : null);
            dto.setMaxPrice(r[4] != null ? ((Number) r[4]).intValue() : null);
            dto.setStartTime((LocalDateTime) r[5]);
            dto.setEndTime((LocalDateTime) r[6]);
            dto.setLocation((String) r[7]);

            // 評分邏輯
            Integer totalScore = r[8] != null ? ((Number) r[8]).intValue() : 0;
            Integer ratingCount = r[9] != null ? ((Number) r[9]).intValue() : 0;
            dto.setRatingCount(ratingCount);
            if (ratingCount > 0) {
                dto.setAverageRatingScore(Math.round((totalScore * 10.0 / ratingCount)) / 10.0);
            } else {
                dto.setAverageRatingScore(0.0);
            }

            result.add(dto);
        }
        return result;
    }
}