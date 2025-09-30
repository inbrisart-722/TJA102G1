// g1_6_search_results.js
// 負責在 search_results.html 載入時，自動呼叫 API /api/exhibitions/search，再把結果渲染到頁面
/* search_results.html搜尋 專用 */

// 依平均分數顯示星星
function renderStars(avg, count) {
    let stars = "";
    const fullStars = Math.floor(avg);             // 整星數
    const hasHalf = avg - fullStars >= 0.1;       // 大於等於 0.1 算半星
    const emptyStars = 5 - fullStars - (hasHalf ? 1 : 0);

    // 整星
    for (let i = 0; i < fullStars; i++) {
        stars += `<i class="icon-star voted"></i>`;
    }

    // 半星
    if (hasHalf) {
        stars += `<i class="icon-star-half-alt voted"></i>`;
    }

    // 空星
    for (let i = 0; i < emptyStars; i++) {
        stars += `<i class="icon-star-empty"></i>`;
    }
	
	// 平均分數顯示到 1 位小數
	    const avgDisplay = avg ? avg.toFixed(1) : "0.0";

    return `${stars} <span><small> ${avgDisplay}</small> &nbsp;(${count || 0})</span>`;
}



document.addEventListener("DOMContentLoaded", () => {
	const filtersForm = document.getElementById("filtersForm");
	const resultsContainer = document.getElementById("cardsContainer"); // 對應你的 list 容器

	const keywordInput = document.getElementById("keywordInput");
	const regionGroup = document.getElementById("regionGroup");

	const fbEcho = document.getElementById("fbSearchEcho");
	const fbKeywordText = document.getElementById("fbKeywordText");
	const fbRegionText = document.getElementById("fbRegionText");
	const fbDateText = document.getElementById("fbDateText");

	const fbRangeInput = document.getElementById("fb-dateRangeInput");
	const fbPopup = document.getElementById("fb-datePopup");
	const fbPrev = document.getElementById("fb-prevMonth");
	const fbNext = document.getElementById("fb-nextMonth");
	const fbMonth = document.getElementById("fb-currentMonth");
	const fbBody = document.getElementById("fb-calendarBody");
	const fbClear = document.getElementById("fb-dateClear");
	const fbOk = document.getElementById("fb-dateConfirm");

	let fbStart = null,
		fbEnd = null,
		fbCur = new Date();

	// ========== 工具 ========== //
	function fmt(d) {
		const y = d.getFullYear(),
			m = String(d.getMonth() + 1).padStart(2, "0"),
			dd = String(d.getDate()).padStart(2, "0");
		return `${y}-${m}-${dd}`;
	}
	function selectedRegions() {
		return Array.from(
			regionGroup.querySelectorAll('input[type="checkbox"]:checked')
		).map((cb) => cb.value);
	}
	function updateEcho() {
		const hk = keywordInput.value.trim() !== "";
		const hr = selectedRegions().length > 0;
		const hd = !!(fbStart || fbEnd);
		fbEcho.style.display = hk || hr || hd ? "block" : "none";
	}

	// ========== 渲染結果 ========== //
	function renderResults(data) {
		data.forEach((exh) => {
			resultsContainer.innerHTML += `
		      <div class="strip_all_tour_list">
		        <a href="/front-end/exhibitions/${exh.exhibitionId}">
		          <div class="row">
		            <div class="col-lg-4 col-md-4">
		              <div class="img_list">
		                <img src="img/0_exhibition/ChatGPT_exhibition_3.png" alt="${exh.exhibitionName}">
		              </div>
		            </div>
		            <div class="col-lg-6 col-md-6">
		              <div class="tour_list_desc">
		                <h3>${exh.exhibitionName}</h3>
		                <p>
		                  <span class="start_time">${new Date(exh.startTime).toLocaleDateString()}</span> ~
		                  <span class="end_time">${new Date(exh.endTime).toLocaleDateString()}</span>
		                </p>
		                <p><span class="location">${exh.location}</span></p>
						<p>
							<span class="rating">
								${renderStars(exh.averageRatingScore, exh.ratingCount)}
							</span>
						</p>
		              </div>
		            </div>
		            <div class="col-lg-2 col-md-2">
		              <div class="price_list">
		                <div><span class="currency">$</span>${exh.minPrice || "-"} - ${exh.maxPrice || "-"}</div>
		              </div>
		            </div>
		          </div>
		        </a>
		      </div>
		    `;
		});
	}

	// ========== 呼叫 API（統一跟 modal.js 一樣 criteria 格式） ========== //
	function fetchResults() {
		const keyword = keywordInput.value.trim();
		const regions = selectedRegions();
		const date_from = fbStart ? fmt(fbStart) : null;
		const date_to = fbEnd ? fmt(fbEnd) : null;

		const criteria = {};
		if (keyword) criteria.keyword = keyword;
		if (regions.length) criteria.regions = regions.join(",");
		if (date_from) criteria.date_from = date_from;
		if (date_to) criteria.date_to = date_to;

		console.log("送出 criteria:", criteria);
		console.log(regions);

		csrfFetch("/api/exhibitions/search", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(criteria)
		})
			.then((res) => {
				console.log("API 狀態碼:", res.status);
				return res.text();  // 先拿原始字串
			})
			.then((txt) => {
				if (!txt) {
					console.warn("API 回傳空字串，改用 []");
					return [];  // 回傳空陣列，避免 JSON.parse 爆掉
				}
				try {
					return JSON.parse(txt); // 嘗試轉成 JSON
				} catch (e) {
					console.error("JSON 解析錯誤:", e, "原始內容:", txt);
					throw e; // 交給 catch 處理
				}
			})
			.then((data) => {
				console.log("API 回傳:", data);
				renderResults(data);
			})
			.catch((err) => {
				console.error("❌ 搜尋 API 失敗:", err);
				resultsContainer.innerHTML = "<p>搜尋失敗，請稍後再試。</p>";
			});
	}

	// ========== 表單送出（即時 fetch） ========== //
	filtersForm.addEventListener("submit", (e) => {
		e.preventDefault();
		if (window.addSearchRecord) {
			const keyword = keywordInput.value.trim();
			const regions = selectedRegions.value.trim();
			const date_from = fbStart ? fmt(fbStart) : null;
			const date_to = fbEnd ? fmt(fbEnd) : null;
			window.addSearchRecord(keyword, regions, date_from, date_to);
		}
		fetchResults();
	});

	// ========== 初始化（讀 URL → 填表單 → 打 API） ========== //
	(function hydrate() {
		const sp = new URLSearchParams(window.location.search);
		const keyword = sp.get("q") || "";
		const rP = sp.get("region") || "";
		const sP = sp.get("start") || "";
		const eP = sp.get("end") || "";

		if (keyword) keywordInput.value = keyword;
		if (rP) {
			const arr = rP.split(",");
			regionGroup
				.querySelectorAll('input[type="checkbox"]')
				.forEach((cb) => (cb.checked = arr.includes(cb.value)));
			fbRegionText.textContent = arr.length ? arr.join("、") : "（未選擇）";
		}
		if (sP) fbStart = new Date(sP);
		if (eP) fbEnd = new Date(eP);
		if (fbStart && fbEnd) {
			const t = `${fmt(fbStart)} ~ ${fmt(fbEnd)}`;
			fbRangeInput.value = t;
			fbDateText.textContent = t;
		} else if (fbStart) {
			const t = `${fmt(fbStart)}（已選起日）`;
			fbRangeInput.value = t;
			fbDateText.textContent = t;
		}

		fbKeywordText.textContent = keywordInput.value.trim() || "（尚未輸入）";
		updateEcho();

		// 頁面一進來就打 API
		fetchResults();
	})();
});
