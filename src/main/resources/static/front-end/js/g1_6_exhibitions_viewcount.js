/* 單一展覽頁 - 瀏覽數統計 */

document.addEventListener("DOMContentLoaded", () => {
	// 從 body 標籤讀取展覽ID
	var exhibitionId = document.body.dataset.exhId;

	// 把請求傳給後端
	fetch(`/api/exhibitionPagePopularityStats/view/${exhibitionId}`, {
		method: "POST"
	})
		.then(res => res.text())
		.then(msg => console.log("瀏覽數統計成功:", msg)) // 成功
		.catch(err => console.error("瀏覽數統計失敗:", err)); // 失敗
});