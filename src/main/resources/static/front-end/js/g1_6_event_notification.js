/**
 * g1_6_event_notification.js
 * 收藏展覽通知用
 *
 * 1. 初始化: 載入後端通知清單 (/api/front-end/protected/notifications/my)
 * 2. 未讀數量徽章: 動態計算未讀數, 控制紅色提示 (badge)
 * 3. 單筆設為已讀
 * 4. 全部設為已讀
 * 
 */

document.addEventListener("DOMContentLoaded", function () {
  const favPanel = document.querySelector("#favorite-notifications"); // 收藏通知面板
  const favBadge = document.getElementById("badge-fav");              // 收藏通知徽章
  const markBtn = document.getElementById("markAllRead");             // 全部已讀按鈕

  /* 工具 */
  // 計算指定面板內的未讀通知數量
  function countUnread(root) {
    return root ? root.querySelectorAll(".notification-unread").length : 0;
  }

  // 更新徽章數字 & 樣式
  function setBadge(badge, n) {
    if (!badge) return;
    badge.textContent = n;
    if (n > 0) {
      badge.classList.remove("d-none");		// 顯示徽章
      badge.classList.add("bg-danger");		// 紅色背景
    } else {
      badge.classList.add("d-none");		// 隱藏徽章
      badge.classList.remove("bg-danger");
    }
  }

  // 計算並刷新收藏通知的徽章數字
  function updateFavCount() {
    setBadge(favBadge, countUnread(favPanel));
  }

  
  /* ========== 1. 載入歷史收藏通知 ========== */
  function loadFavHistory() {
//	console.log("[通知] 呼叫後端 API: /api/front-end/protected/notifications/my");

    csrfFetch(`/api/front-end/protected/notifications/my`)
      .then(res => {
//		console.log("[通知] API 回應狀態:", res.status);
        if (!res.ok) throw new Error("無法載入收藏通知");
        return res.json();
      })
      .then(list => {
//		console.log("[通知] 後端回傳 list =", list);
        favPanel.innerHTML = ""; // 清空舊內容
		
		// 若沒有通知，顯示提示文字
        if (!list || list.length === 0) {
          favPanel.innerHTML = `<p class="text-muted">目前沒有收藏通知</p>`;
          updateFavCount();
          return;
        }

		// 通知卡片
        list.forEach(item => {
//		console.log("[通知] 渲染通知:", item.favoriteAnnouncementId, item.title);

          const card = document.createElement("div");
		  
		  // 根據通知是否已讀，套用不同 class
          card.className = `card mb-3 shadow-sm border-0 ${item.readStatus ? "notification-read" : "notification-unread"}`;
          card.dataset.annId = item.favoriteAnnouncementId; // 後端通知 ID, 供標記已讀使用
		  
          card.innerHTML = `
            <div class="card-body border-0">
              <h5 class="card-title mb-1 fw-bold">${item.title}</h5>
              <p class="card-text mb-2 text-muted" style="font-size:16px">
                ${item.content}
              </p>
            </div>
          `;
		  
          favPanel.appendChild(card);
        });
		
        updateFavCount(); // 更新徽章數字
      })
      .catch(err => console.error("載入收藏通知失敗:", err));
  }

  
  /* ========== 2. 單筆設為已讀 ========== */
  favPanel.addEventListener("click", function (e) {
	// 未讀通知卡片
    const card = e.target.closest(".card.notification-unread");
    if (!card) return;
	
    const annId = card.dataset.annId; // 後端通知 ID
	
    csrfFetch(`/api/front-end/protected/notifications/${annId}/read`, { method: "POST" })
      .then(res => {
        if (!res.ok) throw new Error("標記失敗");
		
		// 把未讀 class 改成已讀
        card.classList.replace("notification-unread", "notification-read");
		
        updateFavCount();
      })
      .catch(err => console.error("標記已讀失敗:", err));
  });

  
  /* ========== 3. 全部設為已讀 ========== */
  if (markBtn) {
    markBtn.addEventListener("click", () => {
      csrfFetch(`/api/front-end/protected/notifications/readAll`, { method: "POST" })
        .then(res => {
          if (!res.ok) throw new Error("全部標記失敗");
		  
		  // 所有未讀通知改成已讀
          favPanel.querySelectorAll(".notification-unread")
            .forEach(card => card.classList.replace("notification-unread", "notification-read"));
			
          updateFavCount();
        })
        .catch(err => console.error("全部已讀失敗:", err));
    });
  }

  /* ========== 初始化 - 載入歷史通知 ========== */
  loadFavHistory();
  
});