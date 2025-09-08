document.addEventListener("DOMContentLoaded", function () {
  ////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////
  // 1st part 調整共用 header / 搜尋匡匡 / 麵包屑 / footer 等板塊
  // 先把 header 標頭 換掉
  const header = document.querySelector("header");
  if (header)
    header.innerHTML = `
  <!-- Header================================================== -->
      <div class="container">
        <div class="row" id="header-row">
          <div class="col-md-2">
            <div id="logo">
              <a href="index.html"
                ><img
                  src="img/logo.png"
                  width="160"
                  height="34"
                  alt="City tours"
                  class="logo_normal"
              /></a>
              <a href="index.html"
                ><img
                  src="img/logo_sticky.png"
                  width="160"
                  height="34"
                  alt="City tours"
                  class="logo_sticky"
              /></a>
            </div>
          </div>
          <nav class="col-md-9">
            <a
              class="cmn-toggle-switch cmn-toggle-switch__htx open_close"
              href="javascript:void(0);"
              ><span>Menu mobile</span></a
            >
            <div class="main-menu">
              <div id="header_menu">
                <img
                  src="img/logo_sticky.png"
                  width="160"
                  height="34"
                  alt="City tours"
                />
              </div>
              <a href="#" class="open_close" id="close_in"
                ><i class="icon_set_1_icon-77"></i
              ></a>
              <ul>
                <li class="submenu">
                  <a href="/html/index.html" class="show-submenu">首頁</a>
                </li>
                <li class="submenu">
                  <a
                    href="/html/all_tours_map_listing.html"
                    class="show-submenu"
                    >地圖探索</a
                  >
                </li>
                <li class="submenu">
                  <a href="/html/all_tours_list.html" class="show-submenu"
                    >推薦展覽</a
                  >
                </li>
              </ul>
            </div>
            <!-- End main-menu -->
            <!-- 搜尋專用 -->
            <div id="nav_icon_1">
              <a href="#" class="search-overlay-menu-btn"
                ><i class="icon_search active"></i
              ></a>
              <input
                class="searchbar"
                type="text"
                id="nav_search"
                placeholder=" 搜尋展覽或主辦名稱"
              />
            </div>
            <!-- 搜尋專用 end -->
            <ul id="top_tools">
              <li id="nav_icon_2">
                <a href="/html/cart.html" class="cart_bt"
                  ><i class="icon-cart"></i><strong id="cart_timer"></strong></a
                >
              </li>
              <li id="nav_icon_3">
                <a href="#"><i class="icon-ticket-2"></i></a>
              </li>
              <li id="nav_icon_4">
                <a href="#"><i class="icon-user"></i></a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
      <!-- container -->`;

  // 先把 search_overlay_menu 滿版搜尋 換掉
  const search_overlay_menu = document.querySelector("div.search-overlay-menu");
  if (search_overlay_menu)
    search_overlay_menu.innerHTML = `	<!-- Search Menu -->
		<span class="search-overlay-close"><i class="icon_set_1_icon-77"></i></span>


		<form role="search" id="searchform" method="get">
			<input value="" name="q" type="text" placeholder="搜尋展覽或主辦名稱 " />
			<button type="submit"><i class="icon_set_1_icon-78"></i></button>
		</form>

		<div id="search_block_parent">
			<!-- ==================== 修改+新增 ==================== -->
			<div id="search_filter_block">
				<!-- 篩選按鈕 -->
				<button id="regionFilterBtn" type="button"><i class="icon-filter"></i> 區域篩選</button>
				<button id="dateFilterBtn" type="button"><i class="icon-filter"></i> 日期篩選</button>

				<!-- 區域篩選彈窗 -->
				<div id="regionFilterPopup" class="region-filter-popup" style="display: none;">
					<div class="region-filter-header">
						<button id="regionClear" class="btn-clear" type="button">清除設定</button>
						<button id="regionSelectAll" class="btn-all" type="button">全選</button>
					</div>
					<div class="region-section">
						<div class="region-options">
							<button class="region-btn" data-region="台北市" type="button">台北市</button>
							<button class="region-btn" data-region="新北市" type="button">新北市</button>
							<button class="region-btn" data-region="桃園市" type="button">桃園市</button>
							<button class="region-btn" data-region="台中市" type="button">台中市</button>
							<button class="region-btn" data-region="台南市" type="button">台南市</button>
							<button class="region-btn" data-region="高雄市" type="button">高雄市</button>
						</div>
					</div>
					<div class="region-footer">
						<button id="regionConfirm" class="btn-confirm" type="button">確認</button>
					</div>
				</div>

				<!-- 日期篩選彈窗 -->
				<div id="dateFilterPopup" class="date-filter-popup" style="display: none;">
					<div class="date-filter-header">
						<button id="dateClear" class="btn-clear" type="button">清除設定</button>
					</div>
					<div class="calendar-container">
						<div class="calendar-header">
							<button id="prevMonth" type="button">‹</button>
							<span id="currentMonth">2025/08</span>
							<button id="nextMonth" type="button">›</button>
						</div>
						<table class="calendar-table">
							<thead>
								<tr>
									<th>日</th>
									<th>一</th>
									<th>二</th>
									<th>三</th>
									<th>四</th>
									<th>五</th>
									<th>六</th>
								</tr>
							</thead>
							<tbody id="calendarBody"></tbody>
						</table>
					</div>
					<div class="date-filter-footer">
						<button id="confirmDate" class="btn-confirm" type="button">確認</button>
					</div>
				</div>
			</div>

			<!-- 回顯欄位：預設隱藏 -->
			<div id="searchEcho" class="mt-3 mb-3" style="display: none;">
				<small id="keywordEcho" class="d-block">
					搜尋關鍵字：<span id="keywordText">（尚未輸入）</span>
				</small>
				<small id="regionEcho" class="d-block">
					區域篩選：<span id="regionText">（未選擇）</span>
				</small>
				<small id="dateEcho" class="d-block">
					日期篩選：<span id="dateText">（未選擇）</span>
				</small>
			</div>

			<!-- ==================== /修改+新增 ==================== -->

			<h3>熱門搜尋</h3>
			<div id="search_hot_block" class="search_block">
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">熱門搜尋</a>
			</div>
			<hr />

			<h3>您最近搜尋過</h3>
			<div id="search_history_block" class="search_block">
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
				<a href="/html/all_tours_list.html" class="history-btn">歷史搜尋</a>
			</div>
		</div>
	<!-- End Search Menu -->`;

  // 把 麵包屑 導覽列 刪除
  const nav_row = document.querySelector("main > div#position");
  if (nav_row) nav_row.remove();

  // footer
  const footer = document.querySelector("footer.revealed");
  if (footer)
    footer.innerHTML = `<div class="container">
        <div class="row" id="footer_row1">
          <div class="col-md-3">
            <h3>【資源】</h3>
            <br />
            <ul>
              <li><a href="#">常見問題 FAQ</a></li>
              <li><a href="#">平台公告</a></li>
              <li><a href="#">主辦方專區</a></li>
            </ul>
          </div>
          <div class="col-md-4">
            <h3>【聯繫我們】</h3>
            <a href="tel://004542344599" id="phone">(02) 813-3782</a>
            <a href="mailto:help@citytours.com" id="email_footer"
              >helper@eventra.com</a
            >
          </div>
        </div>
        <!-- End row -->
        <div class="row" id="footer_row2">
          <div class="col-md-12">
            <div id="social_footer">
              <ul>
                <li>
                  <a href="#0"><i class="bi bi-instagram"></i></a>
                </li>
                <li>
                  <a href="#0"><i class="bi bi-whatsapp"></i></a>
                </li>
                <li>
                  <a href="#0"><i class="bi bi-facebook"></i></a>
                </li>
                <li>
                  <a href="#0"><i class="bi bi-twitter-x"></i></a>
                </li>
                <li>
                  <a href="#0"><i class="bi bi-youtube"></i></a>
                </li>
              </ul>
              <p>© Eventra 2025</p>
            </div>
          </div>
        </div>
        <!-- End row -->
      </div>
      <!-- End container -->`;
  ////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////

  // 搜尋框點擊後滿版顯示（本來只有小 icon 會觸發）
  const el = document.querySelector("div.search-overlay-menu");
  const search_input = document.querySelector("#searchform input");
  const search_block_el = document.querySelector("#nav_icon_1");

  search_block_el.addEventListener("click", function () {
    el.classList.add("open");
    setTimeout(() => search_input.focus(), 50);
  });
  // search bar 也綁定事件去切換是否 sticky 的樣式
  const searchInput = document.getElementById("nav_search");
  const sentinel = document.createElement("div");
  sentinel.style.height = "1px";
  document.body.prepend(sentinel);

  const observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].boundingClientRect.y < 0) {
        searchInput.classList.add("is-sticky");
      } else {
        searchInput.classList.remove("is-sticky");
      }
    },
    { threshold: [1] }
  );

  observer.observe(sentinel);
});

// fetch 部份分開寫
document.addEventListener("DOMContentLoaded", function () {
  // MM:SS => seconds
  function parseTime(str) {
    const [mm, ss] = str.split(":").map(Number);
    return mm * 60 + ss;
  }

  // seconds 轉回 MM:SS 格式
  function formatTime(seconds) {
    if (seconds < 0) seconds = 0;
    const mm = String(Math.floor(seconds / 60)).padStart(2, "0");
    const ss = String(seconds % 60).padStart(2, "0");
    return `${mm}:${ss}`;
  }

  // 右上角背景購物車時間
  fetch("http://localhost:8088/api/cartItem/getMyExpiration", {
    method: "GET",
  })
    .then((res) => {
      if (!res.ok) throw new Error("NOT OK");
      return res.json();
    })
    .then((result) => {
      // result.status => success / failed
      // result.message => 最小背景時間已成功取得 / 最小背景時間取得失敗
      // result.backgroundExpireTime => MM:SS / undefiend
      console.log(result);

      if (result.status === "success") {
        let remaining = parseTime(result.backgroundExpireTime);
        const cart_timer_el = document.querySelector("#cart_timer");
        // 每秒更新
        const intervalId = setInterval(() => {
          remaining--;

          cart_timer_el.innerText = formatTime(remaining);

          if (remaining <= 0) {
            clearInterval(intervalId);
            // 這裡可以觸發 "倒數結束" 的事件，例如 disable 按鈕 / 自動清空
            alert("您的部分購物車明細已過期！");
            cart_timer_el.style.display = "none";
          }
        }, 1000);
        cart_timer_el.style.display = "block";
      }
    })
    .catch((error) => {
      console.log("error");
      console.log(error);
    });
});
