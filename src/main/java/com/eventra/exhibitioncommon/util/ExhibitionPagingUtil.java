package com.eventra.exhibitioncommon.util;

import java.util.*;
import java.util.function.Function;

public class ExhibitionPagingUtil {

    /**
     * 適用於 Object[] 結果，需要 mapper function 轉 DTO
     */
    public static <T> Map<String, Object> buildPagedResult(
            List<Object[]> list,
            int totalElements,
            int page,
            int size,
            Function<Object[], T> mapper) {

        List<T> content = new ArrayList<>();
        for (Object[] r : list) {
            content.add(mapper.apply(r));
        }
        return buildResult(content, totalElements, page, size);
    }

    /**
     * 適用於已經是 DTO 的 list，不需要 mapper
     */
    public static <T> Map<String, Object> buildPagedResult(
            List<T> list,
            int totalElements,
            int page,
            int size) {

        return buildResult(list, totalElements, page, size);
    }

    /* 共用邏輯 */
    private static <T> Map<String, Object> buildResult(
            List<T> content,
            int totalElements,
            int page,
            int size) {

        int totalPages = (int) Math.ceil((double) totalElements / size);

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("page", page);
        result.put("size", size);
        result.put("totalElements", totalElements);
        result.put("totalPages", totalPages);

        return result;
    }
}
