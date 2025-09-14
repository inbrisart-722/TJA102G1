/* 最新展覽頁 */

document.addEventListener("DOMContentLoaded", () => {
	// 抓最新展覽清單顯示區, 即id=latestExhibitionsList
	var container = document.getElementById("latestExhibitionsList");

	// 處理後端傳來的時間(Timestamp), 轉成yyyy-mm-dd 格式
	var formatDate = (ts) => {
		// 如果沒有時間, 回傳空字串
		if (!ts) return '';
		return new Date(ts).toLocaleDateString("zh-TW", { // 台灣時間格式
			year: "numeric",	// 西元年
			month: "2-digit",	// 個位數補零，ex 01 02 03
			day: "2-digit"		// 個位數補零
		});
	};

	// 處理後端傳來的最低/最高價格
	var fmtPrice = (minP, maxP) => {
		if (minP != null && maxP != null) {
			return `<span class="currency">$</span>${Number(minP).toLocaleString('zh-TW')} - ${Number(maxP).toLocaleString('zh-TW')}`;
		} else {
			return ''; // 沒資料時的防呆用
		}
	};

	// 後端API抓熱門展覽清單
	fetch("/api/exhibitions/latest")
		.then(res => res.json()) // 轉成 JSON
		.then(data => {
			console.log(data);

			// 若後端API抓不到資料, 前台顯示無資料結果
			if (!data || data.length === 0) {
				container.innerHTML = "<p>目前沒有最新展覽資料</p>";
				return;
			}

			// 若後端API抓到資料, 用innerHTML加進container
			data.forEach(exh => {
				container.innerHTML += `
					<div class="strip_all_tour_list">
						<a href="/front-end/exhibitions/${exh.exhibitionId}">
							<div class="row">
								<!-- 左邊展覽圖片 -->
								<div class="col-lg-4 col-md-4 position-relative">
									<div class="img_list">
										<img src="img/0_exhibition/ChatGPT_exhibition_1.png" alt="${exh.exhibitionName}">
									</div>
								</div>
								<!-- 右邊展覽資訊 -->
								<div class="col-lg-6 col-md-6">
									<div class="tour_list_desc">
										<h3>${exh.exhibitionName}</h3>
										<p>${formatDate(exh.startTime)} ~ ${formatDate(exh.endTime)}</p>
										<p>${exh.location ?? ''}</p>
										<p>
											<span class="rating">
												<i class="icon-star voted"></i>
												<i class="icon-star voted"></i>
												<i class="icon-star-half-alt voted"></i>
												<i class="icon-star-empty"></i>
												<i class="icon-star-empty"></i>
											</span>(${exh.ratingCount ?? 0})
										</p>
									</div>
								</div>
								
								<!-- 右邊票價 -->
								<div class="col-lg-2 col-md-2">
								  <div class="price_list">
								    <div>${fmtPrice(exh.minPrice, exh.maxPrice)}</div>
								  </div>
								</div>
							</div>
						</a>
					</div>`;
			});
		});
});