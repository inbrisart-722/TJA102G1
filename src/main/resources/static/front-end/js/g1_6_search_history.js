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
					console.warn("回傳不是 JSON:", res);
					return null;
				}
			})
			.then(data =>
				console.log("搜尋紀錄已存 DB:", data)
			)
			.catch(err =>
				console.error("新增搜尋紀錄到 DB 失敗:", err)
			);

	};

	// ===================================================================

	// 前端: 您最近搜尋過 區塊顯示
	const historyBlock = document.getElementById("search_history_block");
	if (historyBlock) {
		function renderHistory() {
			fetch("/api/front-end/protected/member/getMyMemberId")
				.then(res => res.ok ? res.json() : null)
				.then(memberId => {
					if (memberId) {
						// 已登入會員 → 從 DB 撈
						return fetch(`/api/search/recent/member/${memberId}`)
							.then(res => res.json())
							.then(data => data.map(s => s.keyword));
					} else {
						// 未登入 → 用 LocalStorage
						return Promise.resolve(loadKeywordHistory());
					}
				})
				.then(keywords => {
					historyBlock.innerHTML = "";
					if (!keywords || keywords.length === 0) {
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
				})
				.catch(err => console.error("載入搜尋紀錄失敗:", err));
		}
		renderHistory();
	}
});



/* =============================================================
   登入後 LocalStorage 同步 存進 DB (防重複 + 防多次觸發)
   ============================================================= */

// === 建立 key, 用來去重 + 簽章 ===
function makeRecordKey(item) {
	const kw = (item.keyword || "").trim();
	const regions = Array.isArray(item.regions)
		? item.regions.join(",")
		: (item.regions || "");
	const df = item.date_from || "";
	const dt = item.date_to || "";
	return `${kw}|${regions}|${df}|${dt}`;
}

// === 簽章 (簡易 hash) ===
function signatureOf(list) {
	const keys = list.map(makeRecordKey).sort();
	const joined = keys.join(";");
	let hash = 5381 >>> 0;
	for (let i = 0; i < joined.length; i++) {
		hash = (((hash << 5) + hash) + joined.charCodeAt(i)) >>> 0;
	}
	return String(hash);
}

// === 旗標 (防同一頁面多次觸發) ===
function markSessionSynced() {
	sessionStorage.setItem("searchSyncDone", "1");
}
function isSessionSynced() {
	return sessionStorage.getItem("searchSyncDone") === "1";
}

// === 為不同會員記錄最後一次簽章 (防跨頁重送) ===
function getMemberSigKey(memberId) {
	return `searchSyncSig:${memberId}`;
}
function getLastSignature(memberId) {
	return localStorage.getItem(getMemberSigKey(memberId));
}
function setLastSignature(memberId, sig) {
	localStorage.setItem(getMemberSigKey(memberId), sig);
}

window.syncSearchHistoryToDB = function(memberId) {
	if (isSessionSynced()) {
		//		console.log("Session 已同步過");
		return;
	}

	const raw = JSON.parse(localStorage.getItem("searchHistory") || "[]");
	if (!raw.length) {
		//		console.log("沒有可同步的搜尋紀錄");
		markSessionSynced();
		return;
	}

	// 去重複
	const map = new Map();
	for (const item of raw) {
		const key = makeRecordKey(item);
		if (!map.has(key)) map.set(key, item);
	}
	const uniqueList = Array.from(map.values());

	// 比對簽章, 若與上次相同 則不重送
	const currentSig = signatureOf(uniqueList);
	const lastSig = getLastSignature(memberId);
	if (currentSig === lastSig) {
		//		console.log("簽章相同");
		markSessionSynced();
		return;
	}

	// 整理 payload
	const payload = uniqueList.map(item => ({
		keyword: item.keyword || null,
		regions: Array.isArray(item.regions)
			? item.regions.join(",")
			: (item.regions || ""),
		dateFrom: item.date_from || null,
		dateTo: item.date_to || null
	}));

	csrfFetch("/api/search/addBatch", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(payload)
	})
		.then(res => {
			if (!res.ok) throw new Error("HTTP 狀態碼 " + res.status);
			return res.json();
		})
		.then(data => {
			//			console.log("LocalStorage 搜尋紀錄同步到 DB:", data);
			setLastSignature(memberId, currentSig);
			markSessionSynced();
			// 標記已同步
			localStorage.setItem("searchSyncedForMember", memberId);
			//			console.log(`已標記 LocalStorage 為已同步會員 ${memberId}`);

		})
		.catch(err => {
			console.error("同步搜尋紀錄失敗:", err);
		});
};

// 確定登入時觸發一次同步
document.addEventListener("DOMContentLoaded", () => {
	// 嘗試抓取 memberId, 確保 Token 已生效
	let attempts = 0;

	function trySync() {
		fetch("/api/front-end/protected/member/getMyMemberId")
			.then(res => res.ok ? res.json() : Promise.reject())
			.then(memberId => {
				//				console.log("登入會員 ID: ", memberId);
				syncSearchHistoryToDB(memberId);
			})
			.catch(() => {
				if (attempts < 3) {
					attempts++;
					//					console.log(`Token 尚未生效，${attempts} 秒後重試 (${attempts}/3)...`);
					setTimeout(trySync, 1000 * attempts); // 1秒、2秒、3秒後重試
				} else {
					//					console.log("尚未登入，略過搜尋紀錄同步");
				}
			});
	}

	trySync();
});
