package com.eventra.search.model;

import java.sql.Date;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	@Autowired
	private SearchRepository repository; // 基本CRUD 用
	
	private static String nvl(String s) { return s == null ? "" : s; }
    private static String dateStr(Date d) { return d == null ? "" : d.toString(); }


	// 新增單筆搜尋紀錄
	public SearchVO addSearch(SearchVO search) {
		if (search.getRegions() == null) {
			search.setRegions("");
		}
//		System.out.println("收到 keyword=" + search.getKeyword() + ", regions=" + search.getRegions());
		return repository.save(search);
	}

    // 開啟交易, 成功commit(), 失敗rollback(), 前端可再重送請求
	// 批次新增搜尋紀錄, 登入瞬間 LocalStorage 存到 DB 用
    @Transactional
    public List<SearchVO> addSearchBatch(List<SearchVO> searches) {
        if (searches == null || searches.isEmpty()) return Collections.emptyList();

        // 去重複 (同 keyword/regions/dateFrom/dateTo = 同一筆)
        Map<String, SearchVO> uniq = new LinkedHashMap<>();
        for (SearchVO s : searches) {
            if (s.getRegions() == null) s.setRegions("");
            String key = (s.getMemberId() == null ? "0" : s.getMemberId().toString())
                    + "|" + nvl(s.getKeyword())
                    + "|" + nvl(s.getRegions())
                    + "|" + dateStr(s.getDateFrom())
                    + "|" + dateStr(s.getDateTo());
            uniq.put(key, s);
        }

        // 與 DB 同日同條件去重 (避免當日同條件重覆寫入)
        List<SearchVO> toSave = new ArrayList<>();
        for (SearchVO s : uniq.values()) {
            boolean exists = repository.existsSameToday(
                s.getMemberId(),
                s.getKeyword(),
                s.getRegions(),
                s.getDateFrom(),
                s.getDateTo()
            );
            if (!exists) {
                toSave.add(s);
            }
        }

        // 無需寫入就直接回傳空集合
        if (toSave.isEmpty()) {
//            System.out.println("無新紀錄需要寫入（批次已全重複）");
            return Collections.emptyList();
        }

        // 批次寫入
        List<SearchVO> saved = repository.saveAll(toSave);
//        System.out.println("已成功批次新增 " + saved.size() + " 筆搜尋紀錄");
        return saved;
    }

	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
	public List<SearchVO> getRecentSearchesByMember(Integer memberId) {
		return repository.findTop10ByMemberIdOrderBySearchedAtDesc(memberId);
	}

}
