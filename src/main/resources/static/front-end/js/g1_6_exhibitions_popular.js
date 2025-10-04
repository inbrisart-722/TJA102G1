/* 熱門展覽頁 */
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
	// 抓熱門展覽清單顯示區, 即id=popularExhibitionsList
	var container = document.getElementById("popularExhibitionsList");
	const pagination = document.querySelector(".pagination");

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

	// 載入熱門展覽清單
	    function loadPopular(page = 1) {
	        fetch(`/api/exhibitions/popular?page=${page}`)
	            .then(res => res.json())
	            .then(data => {
	                const list = data.content || [];
	                container.innerHTML = "";

	                if (list.length === 0) {
	                    container.innerHTML = "<p>目前沒有熱門展覽資料</p>";
	                    pagination.innerHTML = "";
	                    return;
	                }

	                // 展覽卡片
	                list.forEach(exh => {
	                    container.innerHTML += `
	                        <div class="strip_all_tour_list">
	                            <a href="/front-end/exhibitions/${exh.exhibitionId}">
	                                <div class="row">
	                                    <div class="col-lg-4 col-md-4 position-relative">
	                                        <div class="img_list">
	                                            <img src="${exh.photoPortrait}" alt="${exh.exhibitionName}">
	                                        </div>
	                                    </div>
	                                    <div class="col-lg-6 col-md-6">
	                                        <div class="tour_list_desc">
	                                            <h3>${exh.exhibitionName}</h3>
	                                            <p>${formatDate(exh.startTime)} ~ ${formatDate(exh.endTime)}</p>
	                                            <p>${exh.location ?? ''}</p>
	                                            <p><span class="rating">${renderStars(exh.averageRatingScore, exh.ratingCount)}</span></p>
	                                        </div>
	                                    </div>
	                                    <div class="col-lg-2 col-md-2">
	                                        <div class="price_list">
	                                            <div>${fmtPrice(exh.minPrice, exh.maxPrice)}</div>
	                                        </div>
	                                    </div>
	                                </div>
	                            </a>
	                        </div>`;
	                });

	                // 分頁按鈕
	                renderPagination(data.page, data.totalPages);
	            });
	    }

	    // 動態生成分頁按鈕
		function renderPagination(current, totalPages) {
		    pagination.innerHTML = "";

		    // 上一頁
		    pagination.innerHTML += `
		        <li class="page-item ${current === 1 ? "disabled" : ""}">
		            <a class="page-link" href="#" aria-label="Previous"
		               onclick="event.preventDefault(); if(${current}>1) loadPopular(${current}-1)">
		               &laquo;
		            </a>
		        </li>`;

		    // 頁碼
		    for (let i = 1; i <= totalPages; i++) {
		        pagination.innerHTML += `
		            <li class="page-item ${i === current ? "active" : ""}">
		                <a class="page-link" href="#"
		                   onclick="event.preventDefault(); loadPopular(${i})">${i}</a>
		            </li>`;
		    }

		    // 下一頁
		    pagination.innerHTML += `
		        <li class="page-item ${current === totalPages ? "disabled" : ""}">
		            <a class="page-link" href="#" aria-label="Next"
		               onclick="event.preventDefault(); if(${current}<${totalPages}) loadPopular(${current}+1)">
		               &raquo;
		            </a>
		        </li>`;
		}

	    // 初始化
	    window.loadPopular = loadPopular; // 讓 onclick 找得到
	    loadPopular(1);
	});