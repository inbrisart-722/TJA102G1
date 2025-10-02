/**
 * g1_6_exhibitions_viewcount.js
 * 
 * 展覽頁載入時, 將瀏覽行為回報給後端, 讓系統紀錄瀏覽數
 * 
 * 1. 從 URL 取得 exhibitionId
 * 2. 呼叫 API
 * 3. 後端 Service 決定更新或新增
 * 
 */

document.addEventListener("DOMContentLoaded", () => {
	// 從 Url 抓展覽ID
	const params = new URLSearchParams(window.location.search);
	const exhibitionId = params.get("exhibitionId");

	// 把請求傳給後端
	csrfFetch(`/api/exhibitionPagePopularityStats/count/${exhibitionId}`, {
		method: "POST"
	})
//		.then(res => res.text())
//		.then(msg => console.log("瀏覽數統計成功:", msg))
//		.catch(err => console.error("瀏覽數統計失敗:", err)); 
});