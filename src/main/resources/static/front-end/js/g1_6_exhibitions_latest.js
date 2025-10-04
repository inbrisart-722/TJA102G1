/* 最新展覽頁 */
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

document.addEventListener("DOMContentLoaded", () => {
    var container = document.getElementById("latestExhibitionsList");
    const pagination = document.querySelector(".pagination");

    var formatDate = (ts) => {
        if (!ts) return '';
        return new Date(ts).toLocaleDateString("zh-TW", {
            year: "numeric", month: "2-digit", day: "2-digit"
        });
    };

    var fmtPrice = (minP, maxP) => {
        if (minP != null && maxP != null) {
            return `<span class="currency">$</span>${Number(minP).toLocaleString('zh-TW')} - ${Number(maxP).toLocaleString('zh-TW')}`;
        } else {
            return '';
        }
    };

    // 載入最新展覽清單
    function loadLatest(page = 1) {
        fetch(`/api/exhibitions/latest?page=${page}`)
            .then(res => res.json())
            .then(data => {
                const list = data.content || [];
                container.innerHTML = "";

                if (list.length === 0) {
                    container.innerHTML = "<p>目前沒有最新展覽資料</p>";
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
                                        <div class="img_list" >
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
                   onclick="event.preventDefault(); if(${current}>1) loadLatest(${current}-1)">
                   &laquo;
                </a>
            </li>`;

        // 頁碼
        for (let i = 1; i <= totalPages; i++) {
            pagination.innerHTML += `
                <li class="page-item ${i === current ? "active" : ""}">
                    <a class="page-link" href="#"
                       onclick="event.preventDefault(); loadLatest(${i})">${i}</a>
                </li>`;
        }

        // 下一頁
        pagination.innerHTML += `
            <li class="page-item ${current === totalPages ? "disabled" : ""}">
                <a class="page-link" href="#" aria-label="Next"
                   onclick="event.preventDefault(); if(${current}<${totalPages}) loadLatest(${current}+1)">
                   &raquo;
                </a>
            </li>`;
    }

    // 初始化
    window.loadLatest = loadLatest; // 讓 onclick 找得到
    loadLatest(1);
});
