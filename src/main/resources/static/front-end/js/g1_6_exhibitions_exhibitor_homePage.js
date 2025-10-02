/** g1_6_exhibitions_exhibitor_homePage.js
 * 展商主頁 - 載入該展商所有展覽清單 (支援分頁)
 */

// 依平均分數顯示星星
function renderStars(avg, count) {
    let stars = "";
    const fullStars = Math.floor(avg);
    const hasHalf = avg - fullStars >= 0.1;
    const emptyStars = 5 - fullStars - (hasHalf ? 1 : 0);

    for (let i = 0; i < fullStars; i++) stars += `<i class="icon-star voted"></i>`;
    if (hasHalf) stars += `<i class="icon-star-half-alt voted"></i>`;
    for (let i = 0; i < emptyStars; i++) stars += `<i class="icon-star-empty"></i>`;

    const avgDisplay = avg ? avg.toFixed(1) : "0.0";
    return `${stars} <span><small>${avgDisplay}</small> &nbsp;(${count || 0})</span>`;
}

document.addEventListener("DOMContentLoaded", function () {
	const container = document.querySelector(".col-lg-8 > div > div");
    const pagination = document.querySelector(".pagination");              // 分頁容器
    const exhibitorId = getExhibitorIdFromUrl();

	console.log("exhibitorId = " + exhibitorId);
    if (!exhibitorId) {
        console.error("找不到 exhibitorId，無法載入展覽列表");
        return;
    }

    // 初始化
    window.loadExhibitorExhibitions = loadExhibitorExhibitions;
    loadExhibitorExhibitions(1);

    /* ========== 工具函式 ========== */
    function getExhibitorIdFromUrl() {
        const params = new URLSearchParams(window.location.search);
        return params.get("exhibitorId");
    }

    // 載入展覽清單
    function loadExhibitorExhibitions(page = 1) {
        fetch(`/api/exhibitions/by-exhibitor?exhibitorId=${exhibitorId}&page=${page}`)
            .then(res => res.json())
            .then(data => {
                const list = data.content || [];

                if (list.length === 0) {
                    container.innerHTML = "<p>目前沒有展覽</p>";
                    pagination.innerHTML = "";
                    return;
                }
				
				

                // 展覽卡片
                list.forEach(exh => {
                    const html = `
                        <div class="strip_all_tour_list">
                            <a href="/front-end/exhibitions/${exh.exhibitionId}">
                                <div class="row">
                                    <div class="col-lg-4 col-md-4 position-relative">
                                        <div class="img_list">
                                            <img src="${exh.photoLandscape || 'img/0_exhibition/ChatGPT_exhibition_1.png'}" alt="${exh.exhibitionName}">
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
                    container.insertAdjacentHTML("beforeend", html);
                });

                // 分頁按鈕
                renderPagination(data.page, data.totalPages);
            })
            .catch(err => {
                console.error("載入展覽失敗:", err);
            });
    }

    // 動態生成分頁按鈕
    function renderPagination(current, totalPages) {
        pagination.innerHTML = "";

        // 上一頁
        pagination.innerHTML += `
            <li class="page-item ${current === 1 ? "disabled" : ""}">
                <a class="page-link" href="#" aria-label="Previous"
                   onclick="event.preventDefault(); if(${current}>1) loadExhibitorExhibitions(${current}-1)">
                   &laquo;
                </a>
            </li>`;

        // 頁碼
        for (let i = 1; i <= totalPages; i++) {
            pagination.innerHTML += `
                <li class="page-item ${i === current ? "active" : ""}">
                    <a class="page-link" href="#"
                       onclick="event.preventDefault(); loadExhibitorExhibitions(${i})">${i}</a>
                </li>`;
        }

        // 下一頁
        pagination.innerHTML += `
            <li class="page-item ${current === totalPages ? "disabled" : ""}">
                <a class="page-link" href="#" aria-label="Next"
                   onclick="event.preventDefault(); if(${current}<${totalPages}) loadExhibitorExhibitions(${current}+1)">
                   &raquo;
                </a>
            </li>`;
    }

    // 日期格式化 (yyyy/MM/dd)
    function formatDate(dateStr) {
        if (!dateStr) return "";
        const d = new Date(dateStr);
        return d.getFullYear() + "/" + (d.getMonth() + 1).toString().padStart(2, "0") + "/" + d.getDate().toString().padStart(2, "0");
    }

    // 格式化票價
    function fmtPrice(minP, maxP) {
        if (minP != null && maxP != null) {
            return `<span class="currency">$</span>${Number(minP).toLocaleString('zh-TW')} - ${Number(maxP).toLocaleString('zh-TW')}`;
        } else return '';
    }
});
