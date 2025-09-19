// g1_6_search_history.js
/* 存LocalStorage, 存DB, "您最近搜尋過"區塊顯示 (共用) */
document.addEventListener("DOMContentLoaded", () => {
	const STORAGE_KEY = "searchHistory";       // 完整紀錄
	const KEYWORD_KEY = "searchKeywordHistory"; // keyword-only

	// 讀取LocalStorage 完整紀錄
	function loadHistory() {
		try {
			return JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
		} catch {
			return [];
		}
	}

	// 儲存LocalStorage 完整紀錄
	function saveHistory(list) {
		localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
	}

	// ===================================================================

	// 讀取LocalStorage keyword-only
	function loadKeywordHistory() {
		try {
			return JSON.parse(localStorage.getItem(KEYWORD_KEY)) || [];
		} catch {
			return [];
		}
	}

	// 儲存LocalStorage keyword-only
	function saveKeywordHistory(list) {
		localStorage.setItem(KEYWORD_KEY, JSON.stringify(list));
	}

	// ===================================================================

	// 新增一筆紀錄 (存資訊完整到LocalStorage + DB, 取keyword-only 顯示前端）
	window.addSearchRecord = function(keyword, regions, date_from, date_to) {
		// === 完整紀錄存LocalStorage ===
		const record = {
			keyword: keyword || null,
			regions: regions || [],
			date_from: date_from || null,
			date_to: date_to || null,
			searched_at: new Date().toISOString(),
		};
		let list = loadHistory();
		list.unshift(record);
		saveHistory(list.slice(0, 20)); // 最多保留20筆完整紀錄

		// === 取 keyword-only ===
		if (keyword && keyword.trim() !== "") {
			let keywords = loadKeywordHistory();
			// 去重複, 只保留最新的
			keywords = keywords.filter((k) => k !== keyword);
			keywords.unshift(keyword);
			saveKeywordHistory(keywords.slice(0, 10)); // 最多10筆
		}
		
		// === 同步存 DB ===
		csrfFetch("/api/search/add", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({
				memberId: null, // 非會員，會員的話再改成實際 ID
				keyword: keyword,
				regions: regions && regions.length ? regions.join(",") : "",
				dateFrom: date_from || null,
				dateTo: date_to || null
			})
		})
		.then(res => {
			if (!res.ok) throw new Error("HTTP 狀態碼 " + res.status);
			const contentType = res.headers.get("content-type");
			if (contentType && contentType.includes("application/json")) {
			    return res.json();
			} else {
			    console.warn("⚠️ 回傳不是 JSON:", res);
			    return null;
			}
		})
		.then(data => 
			console.log("✅ 搜尋紀錄已存 DB:", data)
		)
		.catch(err => 
			console.error("❌ 新增搜尋紀錄到 DB 失敗:", err)
		);

	};

	// ===================================================================

	// 前端: 您最近搜尋過 區塊顯示
	const historyBlock = document.getElementById("search_history_block");
	if (historyBlock) {
		function renderHistory() {
			const keywords = loadKeywordHistory();
			historyBlock.innerHTML = "";
			if (keywords.length === 0) {
				historyBlock.innerHTML = "<p>目前沒有搜尋紀錄</p>";
				return;
			}
			keywords.forEach((kw) => {
				const a = document.createElement("a");
				a.href = `/front-end/search_results?keyword=${encodeURIComponent(kw)}`;
				a.className = "history-btn";
				a.textContent = kw;
				historyBlock.appendChild(a);
			});
		}
		renderHistory();
	}
});
