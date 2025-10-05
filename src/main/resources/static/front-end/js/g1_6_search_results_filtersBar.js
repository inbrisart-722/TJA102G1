  /* ===========================
	   展演清單：按「搜尋」才套用 區域/日期/關鍵字顯示；排序即時；樣式高亮
	   - 依據你貼的 HTML 結構 (.col-lg-9、#filtersForm、.sort-tab、Bootstrap 分頁 nav)
	   =========================== */

  /* --- 小工具 --- */
  // 解析日期（支援 YYYY-MM-DD / YYYY/MM/DD）
  function parseDateStr(str) {
    if (!str) return NaN;
    const s = String(str).trim();
    let t = Date.parse(s);
    if (!isNaN(t)) return t;
    const s2 = s.replaceAll("/", "-");
    t = Date.parse(s2);
    return isNaN(t) ? NaN : t;
  }
  // Haversine 距離（公里）
  function haversineKm(lat1, lon1, lat2, lon2) {
    const toRad = (d) => (d * Math.PI) / 180,
      R = 6371;
    const dLat = toRad(lat2 - lat1),
      dLon = toRad(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(toRad(lat1)) *
        Math.cos(toRad(lat2)) *
        Math.sin(dLon / 2) ** 2;
    return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

  /* --- URL 參數工具（不新增歷史紀錄） --- */
  const url = new URL(window.location.href);
  const params = url.searchParams;
  function setParam(key, val) {
    if (val === "" || val == null) params.delete(key);
    else params.set(key, val);
    history.replaceState(null, "", "?" + params.toString()); // 更新目前條目而非新增歷史
  }

  /* --- DOM 參照 --- */
  const colMain = document.querySelector(".col-lg-9");
  const form = document.getElementById("filtersForm");
  const regionSelect = document.getElementById("regionSelect");
  const dateFrom = document.getElementById("dateFrom");
  const dateTo = document.getElementById("dateTo");
  const keywordInput = document.getElementById("keywordInput");
  const clearBtn = document.getElementById("clearBtn");
  const keywordEcho = document.getElementById("keywordEcho");
  const sortTabs = document.querySelectorAll(".sort-tab");
  const pagerNav = document.querySelector(
    '.col-lg-9 nav[aria-label="Page navigation"]'
  );

  /* --- 取得卡片資料（每次呼叫都從 DOM 重新收集，避免「尚未顯示」漏抓） --- */
  function readRating(el) {
    const full = el.querySelectorAll(".icon-star.voted").length;
    const half = el.querySelectorAll(".icon-star-half-alt.voted").length;
    return full + half * 0.5;
  }
  function readCardData(el) {
    const d = el.dataset;
    const startStr = (
      el.querySelector(".start_time")?.textContent || ""
    ).trim();
    const endStr = (
      el.querySelector(".end_time")?.textContent || ""
    ).trim();
    const startTs = parseDateStr(startStr);
    const endTs = parseDateStr(endStr);
    const location = (
      el.querySelector(".location")?.textContent || ""
    ).trim();
    const regionFromLoc = location.slice(0, 3);

    return {
      el,
      title: (el.querySelector("h3")?.textContent || "").trim(),
      location,
      region: d.region || regionFromLoc || "",
      startTs: isNaN(startTs) ? 0 : startTs,
      endTs: isNaN(endTs) ? 0 : endTs,
      popularity: Number(d.popularity || 0),
      rating: isNaN(Number(d.rating)) ? readRating(el) : Number(d.rating),
      createdTs: d.created ? Date.parse(d.created) : 0,
      lat: d.lat ? Number(d.lat) : null,
      lng: d.lng ? Number(d.lng) : null,
      distanceKm: null,
    };
  }
  function collectData() {
    const nodeList =
      colMain?.querySelectorAll(".strip_all_tour_list") || [];
    return Array.from(nodeList).map(readCardData);
  }

  /* --- 使用者座標（僅在「離我最近」時需求） --- */
  let userGeo = { lat: null, lng: null },
    gettingGeo = false;
  async function ensureUserGeo() {
    if (userGeo.lat != null && userGeo.lng != null) return userGeo;
    if (gettingGeo || !("geolocation" in navigator)) return null;
    gettingGeo = true;
    return new Promise((resolve) => {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          userGeo = { lat: pos.coords.latitude, lng: pos.coords.longitude };
          gettingGeo = false;
          resolve(userGeo);
        },
        (_err) => {
          gettingGeo = false;
          resolve(null);
        },
        { enableHighAccuracy: false, timeout: 8000, maximumAge: 600000 }
      );
    });
  }

  /* --- 狀態：只有「按下搜尋」才更新（排序即時） --- */
  const applied = {
    q: params.get("q") || "",
    region: params.get("region") || "",
    from: params.get("from") || "",
    to: params.get("to") || "",
    sort: params.get("sort") || "popularity",
  };

  /* --- UI 載入初值 --- */
  (function init() {
    regionSelect && (regionSelect.value = applied.region);
    dateFrom && (dateFrom.value = applied.from);
    dateTo && (dateTo.value = applied.to);
    keywordInput && (keywordInput.value = params.get("q") || "");
    // 排序高亮
    activateSort(applied.sort);
    // 關鍵字顯示區：顯示「已套用」的字串
    if (keywordEcho)
      keywordEcho.querySelector("span").textContent =
        applied.q || "（尚未輸入）";
    // 初次渲染（依目前已套用狀態）
    applyAll();
  })();

  /* --- 排序 Seg 樣式切換 --- */
  function activateSort(key) {
    sortTabs.forEach((a) => {
      const active = a.dataset.sort === key;
      a.classList.toggle("is-active", active);
      a.setAttribute("aria-current", active ? "true" : "false");
    });
  }

  /* --- 核心：依「已套用狀態 applied」做篩選排序並渲染 --- */
  function applyAll() {
    const data = collectData(); // 每次重新抓，避免延遲載入漏抓
    const fromTs = applied.from ? Date.parse(applied.from) : null;
    const toTs = applied.to ? Date.parse(applied.to) : null;

    // 篩選：區域 + 日期；關鍵字僅顯示在 #keywordEcho，不影響卡片（依你前述需求）
    const filtered = data.filter((item) => {
      const hitRegion = !applied.region || item.region === applied.region;
      const aStart = item.startTs;
      const aEnd = item.endTs || item.startTs;
      const uStart = fromTs ?? -Infinity;
      const uEnd = toTs ?? Infinity;
      const hitDate = !(fromTs || toTs)
        ? true
        : aStart <= uEnd && aEnd >= uStart;
      return hitRegion && hitDate;
    });

    // 排序鍵
    const sortKey = applied.sort || "popularity";

    // 「離我最近」需要距離
    if (sortKey === "nearest") {
      (async () => {
        const ok = await ensureUserGeo();
        if (ok) {
          data.forEach((item) => {
            if (item.lat != null && item.lng != null) {
              item.distanceKm = haversineKm(
                userGeo.lat,
                userGeo.lng,
                item.lat,
                item.lng
              );
            } else {
              item.distanceKm = Infinity;
            }
          });
          render(filtered, sortKey);
        } else {
          applied.sort = "popularity";
          activateSort("popularity");
          render(filtered, "popularity");
        }
      })();
      render(filtered, sortKey); // 先以現況渲染一次
    } else {
      render(filtered, sortKey);
    }
  }

  function render(list, sortKey) {
    if (!colMain) return;
    const parentBefore = document.querySelector(
      '.col-lg-9 nav[aria-label="Page navigation"]'
    );

    const sorted = list.slice().sort((a, b) => {
      switch (sortKey) {
        case "rating":
          return b.rating - a.rating;
        case "newest":
          return b.createdTs - a.createdTs;
        case "nearest":
          return (a.distanceKm ?? Infinity) - (b.distanceKm ?? Infinity);
        case "popularity":
        default:
          return b.popularity - a.popularity;
      }
    });

    // 先全部隱藏
    const allCards = colMain.querySelectorAll(".strip_all_tour_list");
    allCards.forEach((c) => (c.style.display = "none"));

    // 依序附掛到分頁 nav 之前
    sorted.forEach((item) => {
      item.el.style.display = "";
      parentBefore
        ? colMain.insertBefore(item.el, parentBefore)
        : colMain.appendChild(item.el);
    });
  }

  /* --- 事件：按下「搜尋」才更新已套用狀態 applied --- */
  form?.addEventListener("submit", (e) => {
    e.preventDefault();
    console.log("filtersBar submit → 轉交主搜尋");

    // 交給主搜尋程式處理，不在這邊更新狀態
    if (typeof window.fetchResults === "function") {
      window.fetchResults(1);
    } else {
      console.warn("⚠️ g1_6_search_results.js 尚未載入或 fetchResults 未定義");
    }
  });


  /* --- 排序 Seg：即時切換（不必按搜尋） --- */
  sortTabs.forEach((a) => {
    a.addEventListener("click", (e) => {
      e.preventDefault();
      applied.sort = a.dataset.sort;
      activateSort(applied.sort);
      setParam("sort", applied.sort);
      applyAll();
    });
  });

  /* --- 清除：重置表單，但不動排序 --- */
  clearBtn?.addEventListener("click", () => {
    regionSelect && (regionSelect.value = "");
    dateFrom && (dateFrom.value = "");
    dateTo && (dateTo.value = "");
    keywordInput && (keywordInput.value = "");
    applied.q = applied.region = applied.from = applied.to = "";
    if (keywordEcho)
      keywordEcho.querySelector("span").textContent = "（尚未輸入）";
    setParam("q", "");
    setParam("region", "");
    setParam("from", "");
    setParam("to", "");
    applyAll();
  });