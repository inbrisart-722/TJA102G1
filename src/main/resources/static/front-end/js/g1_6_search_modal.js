// g1_6_search_modal.js
// 負責收集條件、送 API、導頁
/* 所有頁面於header的彈窗搜尋 專用 */
document.addEventListener("DOMContentLoaded", function() {
	const overlay = document.querySelector(".search-overlay-menu");
	if (!overlay) return;

	const searchForm = overlay.querySelector("#searchform");
	const searchInput = searchForm.querySelector("input[name='q']");
	const regionBtn = overlay.querySelector("#regionFilterBtn");
	const dateBtn = overlay.querySelector("#dateFilterBtn");
	const regionPopup = overlay.querySelector("#regionFilterPopup");
	const datePopup = overlay.querySelector("#dateFilterPopup");

	const regionBtns = overlay.querySelectorAll(".region-btn");
	const regionClear = overlay.querySelector("#regionClear");
	const regionAll = overlay.querySelector("#regionSelectAll");
	const regionOk = overlay.querySelector("#regionConfirm");

	const dateClear = overlay.querySelector("#dateClear");
	const dateOk = overlay.querySelector("#confirmDate");
	const calBody = overlay.querySelector("#calendarBody");
	const monthLabel = overlay.querySelector("#currentMonth");
	const prevMonthBtn = overlay.querySelector("#prevMonth");
	const nextMonthBtn = overlay.querySelector("#nextMonth");

	const searchEcho = overlay.querySelector("#searchEcho");
	const keywordText = overlay.querySelector("#keywordText");
	const regionText = overlay.querySelector("#regionText");
	const dateText = overlay.querySelector("#dateText");

	let selectedRegions = [];
	let startDate = null,
		endDate = null;
	let currentDate = new Date();

	function fmt(d) {
	    const year = d.getFullYear();
	    const month = String(d.getMonth() + 1).padStart(2, "0");
	    const day = String(d.getDate()).padStart(2, "0");
	    return `${year}-${month}-${day}`;
	}

	function updateEcho() {
		const hasK = searchInput.value.trim() !== "";
		const hasR = selectedRegions.length > 0;
		const hasD = startDate && endDate;
		searchEcho.style.display = hasK || hasR || hasD ? "block" : "none";
	}

	// 關鍵字即時顯示
	searchInput.addEventListener("input", () => {
		keywordText.textContent = searchInput.value.trim() || "（尚未輸入）";
		updateEcho();
	});

	// 區域彈窗控制
	regionBtn.addEventListener("click", (e) => {
		e.stopPropagation();
		const show = regionPopup.style.display !== "block";
		regionPopup.style.display = show ? "block" : "none";
		datePopup.style.display = "none";
	});
	regionBtns.forEach((b) => {
		b.addEventListener("click", () => {
			const r = b.dataset.region;
			const i = selectedRegions.indexOf(r);
			if (i > -1) {
				selectedRegions.splice(i, 1);
				b.classList.remove("active");
			} else {
				selectedRegions.push(r);
				b.classList.add("active");
			}
		});
	});
	regionAll.addEventListener("click", () => {
		selectedRegions = [];
		regionBtns.forEach((b) => {
			b.classList.add("active");
			selectedRegions.push(b.dataset.region);
		});
	});
	regionClear.addEventListener("click", () => {
		selectedRegions = [];
		regionBtns.forEach((b) => b.classList.remove("active"));
		regionText.textContent = "（未選擇）";
		updateEcho();
	});
	regionOk.addEventListener("click", () => {
		regionPopup.style.display = "none";
		regionText.textContent = selectedRegions.length
			? selectedRegions.join("、")
			: "（未選擇）";
		updateEcho();
	});

	// 日期彈窗控制
	dateBtn.addEventListener("click", (e) => {
		e.stopPropagation();
		const show = datePopup.style.display !== "block";
		datePopup.style.display = show ? "block" : "none";
		regionPopup.style.display = "none";
		if (show) renderCalendar();
	});
	function renderCalendar() {
		const y = currentDate.getFullYear(),
			m = currentDate.getMonth();
		monthLabel.textContent = `${y}/${String(m + 1).padStart(2, "0")}`;
		const first = new Date(y, m, 1).getDay(),
			days = new Date(y, m + 1, 0).getDate();
		calBody.innerHTML = "";
		let row = document.createElement("tr");

		for (let i = 0; i < first; i++) row.appendChild(document.createElement("td"));

		for (let d = 1; d <= days; d++) {
			const td = document.createElement("td");
			td.textContent = d;
			const thisDate = new Date(y, m, d);

			td.classList.toggle("range-start", startDate && thisDate.toDateString() === startDate.toDateString());
			td.classList.toggle("range-end", endDate && thisDate.toDateString() === endDate.toDateString());
			if (startDate && endDate && thisDate >= startDate && thisDate <= endDate)
				td.classList.add("in-range");

			td.addEventListener("click", (e) => {
				e.stopPropagation();
				if (!startDate || (startDate && endDate)) {
					startDate = thisDate;
					endDate = null;
				} else if (thisDate < startDate) {
					endDate = startDate;
					startDate = thisDate;
				} else {
					endDate = thisDate;
				}
				renderCalendar();
			});

			row.appendChild(td);
			if ((first + d) % 7 === 0 || d === days) {
				calBody.appendChild(row);
				row = document.createElement("tr");
			}
		}
	}
	prevMonthBtn.addEventListener("click", () => {
		currentDate.setMonth(currentDate.getMonth() - 1);
		renderCalendar();
	});
	nextMonthBtn.addEventListener("click", () => {
		currentDate.setMonth(currentDate.getMonth() + 1);
		renderCalendar();
	});
	dateClear.addEventListener("click", () => {
		startDate = endDate = null;
		dateText.textContent = "（未選擇）";
		renderCalendar();
		updateEcho();
	});
	dateOk.addEventListener("click", () => {
		datePopup.style.display = "none";
		dateText.textContent =
			startDate && endDate ? `${fmt(startDate)} ~ ${fmt(endDate)}` : "（未選擇）";
		updateEcho();
	});

	// 點外面關閉彈窗
	document.addEventListener("click", (e) => {
		if (regionPopup.style.display === "block" && !regionPopup.contains(e.target) && e.target !== regionBtn)
			regionPopup.style.display = "none";
		if (datePopup.style.display === "block" && !datePopup.contains(e.target) && e.target !== dateBtn)
			datePopup.style.display = "none";
	});

	// addSearchRecord + 導頁
	searchForm.addEventListener("submit", (e) => {
		e.preventDefault();
		console.log("index.html 彈窗搜尋觸發");
		const keyword = searchInput.value.trim();
		const regions = selectedRegions;
		const date_from = startDate ? fmt(startDate) : null;
		const date_to = endDate ? fmt(endDate) : null;

		// 呼叫 addSearchRecord (g1_6_search_history.js, 存 LocalStorage + DB)
		if (window.addSearchRecord) {
			window.addSearchRecord(keyword, regions, date_from, date_to);
		}

		// 先打 API，確認有結果再跳轉
		const criteria = {};
		if (keyword) criteria.keyword = keyword;
		if (regions.length) criteria.regions = regions.join(",");
		if (date_from) criteria.date_from = date_from;
		if (date_to) criteria.date_to = date_to;

		console.log("送出 criteria:", criteria);

		// 先打 API，確認有結果
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

				// 再導頁，把條件帶去 search_results.html
				const qs = new URLSearchParams();
				if (keyword) qs.append("q", keyword);
				if (regions.length) qs.append("region", regions.join(","));
				if (date_from) qs.append("start", date_from);
				if (date_to) qs.append("end", date_to);

				window.location.href = `/front-end/search_results?${qs.toString()}`;
			})
			.catch((err) => {
				console.error("搜尋 API 失敗:", err);
				alert("搜尋失敗，請稍後再試");
			});
	});

});
