// g1_6_favorite_admin.js
// 專門處理 會員中心 - 收藏清單列表載入與切換收藏

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

    return `${stars} <span><small>&nbsp;${avgDisplay} &nbsp;&nbsp;(${count || 0})</small></span>`;
}



// 收藏清單列表載入
document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("favListContainer");
    if (!container) return;

    // 呼叫 API 載入收藏清單
    csrfFetch(`/api/front-end/protected/favorite/list`)
        .then(res => res.json())
        .then(data => {
            container.innerHTML = ""; // 清空舊內容
			
            if (!data || data.length === 0) {
                container.innerHTML = `<p>目前沒有收藏任何展覽。</p>`;
                return;
            }

            data.forEach(fav => {
                const card = document.createElement("div");
                card.className = "col-lg-4 col-md-6";

                card.innerHTML = `
                    <div class="hotel_container">
                        <div class="img_container" style="height:267.83px">>
                            <a href="/front-end/exhibitions/${fav.exhibitionId}">
                                <img src="${fav.photoPortrait}" 
                                     width="800" height="533" class="img-fluid" alt="${fav.exhibitionName}"/>
                                <div class="short_info hotel"></div>
                            </a>
                        </div>
                        <div class="hotel_title">
                            <h3>${fav.exhibitionName}</h3>
							<div class="rating">
                                ${renderStars(fav.averageRatingScore, fav.totalRatingCount)}
                            </div>
                            <div>
                                <button type="button"
                                    style="border: none; background: transparent; padding: 0;"
                                    class="wishlist_close_admin btn_fav"
                                    data-exh-id="${fav.exhibitionId}">
                                    -
                                </button>
                            </div>
                        </div>
                    </div>
                `;

                container.appendChild(card);
            });
        })
        .catch(err => {
            console.error("載入收藏清單失敗:", err);
            container.innerHTML = `<p>載入失敗，請稍後再試。</p>`;
        });
});

// 綁定收藏切換 (取消後移除)
document.addEventListener("click", (e) => {
    if (!e.target.closest(".btn_fav")) return;

    const $btn = e.target.closest(".btn_fav");
	const exhId = $btn.dataset.exhId;   // ← 修正這裡

    csrfFetch("/api/front-end/protected/favorite/toggle", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `exhId=${exhId}`
    })
        .then(res => res.json())
        .then(data => {
            if (!data.favoriteStatus) {
                // 取消收藏, 從頁面移除卡片
                const card = $btn.closest(".col-lg-4.col-md-6");
                if (card) {
                    card.style.transition = "opacity 0.3s";
                    card.style.opacity = "0";
                    setTimeout(() => card.remove(), 300);
                }
            }
        })
        .catch(err => {
            console.error("切換收藏失敗:", err);
        });
});