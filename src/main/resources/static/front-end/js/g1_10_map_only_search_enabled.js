document.addEventListener("DOMContentLoaded", function () {
	/* 搜尋彈窗 */
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

						  <div class="region-block">
						    <h6><b>北部地區</b></h6>
						    <div class="region-options">
						      <button class="region-btn" data-region="台北">台北</button>
						      <button class="region-btn" data-region="新北">新北</button>
						      <button class="region-btn" data-region="基隆">基隆</button>
						      <button class="region-btn" data-region="桃園">桃園</button>
						      <button class="region-btn" data-region="新竹">新竹</button>
						    </div>
						  </div>
						  <p></p>

						  <div class="region-block">
						    <h6><b>中部地區</b></h6>
						    <div class="region-options">
						      <button class="region-btn" data-region="苗栗">苗栗</button>
						      <button class="region-btn" data-region="台中">台中</button>
						      <button class="region-btn" data-region="彰化">彰化</button>
						      <button class="region-btn" data-region="南投">南投</button>
						      <button class="region-btn" data-region="雲林">雲林</button>
						    </div>
						  </div>
						  <p></p>

						  <div class="region-block">
						    <h6><b>南部地區</b></h6>
						    <div class="region-options">
						      <button class="region-btn" data-region="嘉義">嘉義</button>
						      <button class="region-btn" data-region="台南">台南</button>
						      <button class="region-btn" data-region="高雄">高雄</button>
						      <button class="region-btn" data-region="屏東">屏東</button>
						    </div>
						  </div>
						  <p></p>

						  <div class="region-block">
						    <h6><b>東部地區</b></h6>
						    <div class="region-options">
						      <button class="region-btn" data-region="宜蘭">宜蘭</button>
						      <button class="region-btn" data-region="花蓮">花蓮</button>
						      <button class="region-btn" data-region="台東">台東</button>
						    </div>
						  </div>
						  <p></p>

						  <div class="region-block">
						    <h6><b>離島地區</b></h6>
						    <div class="region-options">
						      <button class="region-btn" data-region="澎湖">澎湖</button>
						      <button class="region-btn" data-region="金門">金門</button>
						      <button class="region-btn" data-region="馬祖">馬祖</button>
						    </div>
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
				</div>
			</div>
		<!-- End Search Menu -->`;
	
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