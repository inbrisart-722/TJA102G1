/** g1_6_exhibitions_exhibitor_homePage.js
 * 展商主頁 - 載入該展商所有展覽清單
 */

document.addEventListener("DOMContentLoaded", function () {
    const exhibitionListContainer = document.querySelector(".col-lg-9 > div"); // 展覽卡片列表區塊
    const exhibitorId = getExhibitorIdFromUrl(); // 從 URL 取出展商 ID

    if (!exhibitorId) {
        console.error("找不到 exhibitorId，無法載入展覽列表");
        return;
    }

    // 呼叫 API
    csrfFetch(`/api/exhibitions/by-exhibitor?exhibitorId=${exhibitorId}`)
        .then(res => res.json())
        .then(data => {
            renderExhibitions(data);
        })
        .catch(err => {
            console.error("載入展覽失敗:", err);
        });

    /* ========== 工具函式 ========== */
    // 取 URL 參數的 exhibitorId
    function getExhibitorIdFromUrl() {
        const params = new URLSearchParams(window.location.search);
        return params.get("exhibitorId");
    }

    // 渲染展覽卡片
    function renderExhibitions(exhibitions) {
        exhibitionListContainer.innerHTML = ""; // 清空原本假資料

        if (!exhibitions || exhibitions.length === 0) {
            exhibitionListContainer.innerHTML = "<p>目前沒有展覽</p>";
            return;
        }

        exhibitions.forEach(exh => {
            const html = `
                <div class="strip_all_tour_list">
                    <a href="/front-end/exhibitions/${exh.exhibitionId}">
                        <div class="row">
                            <div class="col-lg-4 col-md-4 position-relative">
                                <div class="img_list">
                                    <img src="${exh.photoLandscape}" alt="Image">
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6">
                                <div class="tour_list_desc">
                                    <div>
                                        <h3>${exh.exhibitionName}</h3>
                                        <p>
                                            <span class="start_time">${formatDate(exh.startTime)}</span> ~ 
                                            <span class="end_time">${formatDate(exh.endTime)}</span>
                                        </p>
                                        <p><span class="location">${exh.location}</span></p>
                                        <p>
                                            <span class="rating">
                                                ${renderStars(exh.ratingCount)}
                                            </span> (${exh.ratingCount})
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-2">
                                <div class="price_list">
                                    <div>
                                        <span class="currency">$</span>${exh.minPrice} - ${exh.maxPrice}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </a>
                </div>
            `;
            exhibitionListContainer.insertAdjacentHTML("beforeend", html);
        });
    }

    // 日期格式化 (yyyy/MM/dd)
    function formatDate(dateStr) {
        if (!dateStr) return "";
        const d = new Date(dateStr);
        return d.getFullYear() + "/" + (d.getMonth() + 1).toString().padStart(2, "0") + "/" + d.getDate().toString().padStart(2, "0");
    }

    // 星星顯示 (暫時用 ratingCount 當樣本，後續可改 avgRating)
    function renderStars(ratingCount) {
        const starCount = ratingCount > 5 ? 5 : ratingCount; // 假裝 <=5
        let stars = "";
        for (let i = 0; i < 5; i++) {
            if (i < starCount) {
                stars += '<i class="icon-star voted"></i>';
            } else {
                stars += '<i class="icon-star-empty"></i>';
            }
        }
        return stars;
    }
});