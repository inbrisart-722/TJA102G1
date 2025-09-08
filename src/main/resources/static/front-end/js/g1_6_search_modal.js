// ==================== /修改+新增 ====================
document.addEventListener("DOMContentLoaded", function () {
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
    return d.toISOString().split("T")[0];
  }
  function updateEcho() {
    const hasK = searchInput.value.trim() !== "";
    const hasR = selectedRegions.length > 0;
    const hasD = startDate && endDate;
    searchEcho.style.display = hasK || hasR || hasD ? "block" : "none";
  }

  // 關鍵字即時
  searchInput.addEventListener("input", () => {
    keywordText.textContent = searchInput.value.trim() || "（尚未輸入）";
    updateEcho();
  });

  // 區域彈窗
  regionBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    const show = regionPopup.style.display !== "block";
    regionPopup.style.display = show ? "block" : "none";
    datePopup.style.display = "none";
    if (show) {
      const rect = regionBtn.getBoundingClientRect();
      regionPopup.style.top = `${rect.bottom + window.scrollY + 6}px`;
      regionPopup.style.left = `${
        rect.right + window.scrollX - regionPopup.offsetWidth
      }px`;
    }
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

  // 日期彈窗
  dateBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    const show = datePopup.style.display !== "block";
    datePopup.style.display = show ? "block" : "none";
    regionPopup.style.display = "none";
    if (show) {
      const rect = dateBtn.getBoundingClientRect();
      datePopup.style.top = `${rect.bottom + window.scrollY + 6}px`;
      datePopup.style.left = `${rect.left + window.scrollX}px`;
      renderCalendar();
    }
  });
  function renderCalendar() {
    const y = currentDate.getFullYear(),
      m = currentDate.getMonth();
    monthLabel.textContent = `${y}/${String(m + 1).padStart(2, "0")}`;
    const first = new Date(y, m, 1).getDay(),
      days = new Date(y, m + 1, 0).getDate();
    calBody.innerHTML = "";
    let row = document.createElement("tr");
    for (let i = 0; i < first; i++)
      row.appendChild(document.createElement("td"));
    for (let d = 1; d <= days; d++) {
      const td = document.createElement("td");
      td.textContent = d;
      const thisDate = new Date(y, m, d);
      td.classList.toggle(
        "range-start",
        startDate && thisDate.toDateString() === startDate.toDateString()
      );
      td.classList.toggle(
        "range-end",
        endDate && thisDate.toDateString() === endDate.toDateString()
      );
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
  prevMonthBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
  });
  nextMonthBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
  });
  dateClear.addEventListener("click", (e) => {
    e.stopPropagation();
    startDate = endDate = null;
    dateText.textContent = "（未選擇）";
    renderCalendar();
    updateEcho();
  });
  dateOk.addEventListener("click", (e) => {
    e.stopPropagation();
    datePopup.style.display = "none";
    dateText.textContent =
      startDate && endDate
        ? `${fmt(startDate)} ~ ${fmt(endDate)}`
        : "（未選擇）";
    updateEcho();
  });

  // 點外面關閉彈窗
  document.addEventListener("click", (e) => {
    if (
      regionPopup.style.display === "block" &&
      !regionPopup.contains(e.target) &&
      e.target !== regionBtn
    )
      regionPopup.style.display = "none";
    if (
      datePopup.style.display === "block" &&
      !datePopup.contains(e.target) &&
      e.target !== dateBtn
    )
      datePopup.style.display = "none";
  });

  // 提交：導到搜尋結果頁
  searchForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const qs = new URLSearchParams();
    const q = searchInput.value.trim();
    if (q) qs.append("q", q);
    if (selectedRegions.length) qs.append("region", selectedRegions.join(","));
    if (startDate) qs.append("start", fmt(startDate));
    if (endDate) qs.append("end", fmt(endDate));
    window.location.href = `/html/search_results.html?${qs.toString()}`;
  });
});
