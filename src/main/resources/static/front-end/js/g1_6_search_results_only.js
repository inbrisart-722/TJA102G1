// <!-- 搜尋頁用 -->
document.addEventListener("DOMContentLoaded", () => {
  const filtersForm = document.getElementById("filtersForm");
  if (!filtersForm) return;

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

  // 即時回顯
  keywordInput.addEventListener("input", () => {
    fbKeywordText.textContent = keywordInput.value.trim() || "（尚未輸入）";
    updateEcho();
  });
  regionGroup.addEventListener("change", () => {
    const r = selectedRegions();
    fbRegionText.textContent = r.length ? r.join("、") : "（未選擇）";
    updateEcho();
  });

  // 月曆
  function renderFbCal() {
    const y = fbCur.getFullYear(),
      m = fbCur.getMonth();
    fbMonth.textContent = `${y}/${String(m + 1).padStart(2, "0")}`;
    const first = new Date(y, m, 1).getDay(),
      days = new Date(y, m + 1, 0).getDate();
    fbBody.innerHTML = "";
    let row = document.createElement("tr");
    for (let i = 0; i < first; i++)
      row.appendChild(document.createElement("td"));
    for (let d = 1; d <= days; d++) {
      const td = document.createElement("td");
      td.textContent = d;
      const thisDate = new Date(y, m, d);
      td.classList.toggle(
        "range-start",
        fbStart && thisDate.toDateString() === fbStart.toDateString()
      );
      td.classList.toggle(
        "range-end",
        fbEnd && thisDate.toDateString() === fbEnd.toDateString()
      );
      if (fbStart && fbEnd && thisDate >= fbStart && thisDate <= fbEnd)
        td.classList.add("in-range");
      td.addEventListener("click", (e) => {
        e.stopPropagation();
        if (!fbStart || (fbStart && fbEnd)) {
          fbStart = thisDate;
          fbEnd = null;
        } else if (thisDate < fbStart) {
          fbEnd = fbStart;
          fbStart = thisDate;
        } else {
          fbEnd = thisDate;
        }
        // 即時顯示
        if (fbStart && fbEnd) {
          const t = `${fmt(fbStart)} ~ ${fmt(fbEnd)}`;
          fbRangeInput.value = t;
          fbDateText.textContent = t;
        } else if (fbStart) {
          const t = `${fmt(fbStart)}（已選起日）`;
          fbRangeInput.value = t;
          fbDateText.textContent = t;
        } else {
          fbRangeInput.value = "";
          fbDateText.textContent = "（未選擇）";
        }
        updateEcho();
        renderFbCal();
      });
      row.appendChild(td);
      if ((first + d) % 7 === 0 || d === days) {
        fbBody.appendChild(row);
        row = document.createElement("tr");
      }
    }
  }

  function openFbPopup() {
    fbPopup.style.display = "block";
    renderFbCal();
  }
  function closeFbPopup() {
    fbPopup.style.display = "none";
  }

  fbRangeInput.addEventListener("click", (e) => {
    e.stopPropagation();
    openFbPopup();
  });
  fbPrev.addEventListener("click", (e) => {
    e.stopPropagation();
    fbCur.setMonth(fbCur.getMonth() - 1);
    renderFbCal();
  });
  fbNext.addEventListener("click", (e) => {
    e.stopPropagation();
    fbCur.setMonth(fbCur.getMonth() + 1);
    renderFbCal();
  });
  fbClear.addEventListener("click", (e) => {
    e.stopPropagation();
    fbStart = fbEnd = null;
    fbRangeInput.value = "";
    fbDateText.textContent = "（未選擇）";
    updateEcho();
    renderFbCal();
  });
  fbOk.addEventListener("click", (e) => {
    e.stopPropagation();
    closeFbPopup();
  });
  document.addEventListener("click", (e) => {
    if (
      fbPopup.style.display === "block" &&
      !fbPopup.contains(e.target) &&
      e.target !== fbRangeInput
    )
      closeFbPopup();
  });

  // 送出到搜尋結果頁
  filtersForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const q = keywordInput.value.trim();
    const regions = selectedRegions();
    const from = fbStart ? fmt(fbStart) : "";
    const to = fbEnd ? fmt(fbEnd) : "";
    const qs = new URLSearchParams();
    if (q) qs.append("q", q);
    if (regions.length) qs.append("region", regions.join(","));
    if (from) qs.append("start", from);
    if (to) qs.append("end", to);
    const activeSort = document.querySelector(".sort-tabs .sort-tab.is-active");
    if (activeSort && activeSort.dataset.sort)
      qs.append("sort", activeSort.dataset.sort);

    // 記住使用者最後一次 filtersBar 搜尋
    try {
      localStorage.setItem(
        "filtersBar:lastSearch",
        JSON.stringify({
          q,
          region: regions,
          from,
          to,
          sort: (activeSort && activeSort.dataset.sort) || "",
        })
      );
    } catch (_) {}
    window.location.href = `/html/search_results.html?${qs.toString()}`;
  });

  // 排序即時切換（保留 URL 參數一致性可再加）
  document.querySelectorAll(".sort-tabs .sort-tab").forEach((tab) => {
    tab.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".sort-tabs .sort-tab").forEach((t) => {
        t.classList.remove("is-active");
        t.removeAttribute("aria-current");
      });
      tab.classList.add("is-active");
      tab.setAttribute("aria-current", "true");
    });
  });

  // 初始化（URL 優先，再退回 localStorage）
  (function hydrate() {
    const sp = new URLSearchParams(window.location.search);
    const q = sp.get("q") || "";
    const rP = sp.get("region") || "";
    const sP = sp.get("start") || "";
    const eP = sp.get("end") || "";
    const sortP = sp.get("sort") || "";

    let used = false;
    if (q || rP || sP || eP || sortP) {
      used = true;
      keywordInput.value = q;
      if (rP) {
        const arr = rP.split(",");
        regionGroup
          .querySelectorAll('input[type="checkbox"]')
          .forEach((cb) => (cb.checked = arr.includes(cb.value)));
        fbRegionText.textContent = arr.length ? arr.join("、") : "（未選擇）";
      }
      if (sP) {
        fbStart = new Date(sP);
      }
      if (eP) {
        fbEnd = new Date(eP);
      }
      if (fbStart && fbEnd) {
        const t = `${fmt(fbStart)} ~ ${fmt(fbEnd)}`;
        fbRangeInput.value = t;
        fbDateText.textContent = t;
      } else if (fbStart) {
        const t = `${fmt(fbStart)}（已選起日）`;
        fbRangeInput.value = t;
        fbDateText.textContent = t;
      }
      if (sortP) {
        document.querySelectorAll(".sort-tabs .sort-tab").forEach((t) => {
          t.classList.remove("is-active");
          t.removeAttribute("aria-current");
          if (t.dataset.sort === sortP) {
            t.classList.add("is-active");
            t.setAttribute("aria-current", "true");
          }
        });
      }
    }
    // if (!used) {
    // 	try {
    // 		const raw = localStorage.getItem("filtersBar:lastSearch");
    // 		if (raw) {
    // 			const last = JSON.parse(raw);
    // 			keywordInput.value = last.q || "";
    // 			const arr = Array.isArray(last.region) ? last.region : (last.region ? String(last.region).split(",") : []);
    // 			regionGroup.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = arr.includes(cb.value));
    // 			fbRegionText.textContent = arr.length ? arr.join("、") : "（未選擇）";
    // 			if (last.from) { fbStart = new Date(last.from); }
    // 			if (last.to) { fbEnd = new Date(last.to); }
    // 			if (fbStart && fbEnd) { const t = `${fmt(fbStart)} ~ ${fmt(fbEnd)}`; fbRangeInput.value = t; fbDateText.textContent = t; }
    // 			else if (fbStart) { const t = `${fmt(fbStart)}（已選起日）`; fbRangeInput.value = t; fbDateText.textContent = t; }
    // 			if (last.sort) {
    // 				document.querySelectorAll(".sort-tabs .sort-tab").forEach(t => {
    // 					t.classList.remove("is-active"); t.removeAttribute("aria-current");
    // 					if (t.dataset.sort === last.sort) { t.classList.add("is-active"); t.setAttribute("aria-current", "true"); }
    // 				});
    // 			}
    // 		}
    // 	} catch (_) { }
    // }
    fbKeywordText.textContent = keywordInput.value.trim() || "（尚未輸入）";
    updateEcho();
    renderFbCal();
  })();
});
