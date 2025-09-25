// g1_6_event_notification.js

// 收藏展覽通知 - 後端 API 已改用 SecurityContextHolder，不需前端傳 memId

document.addEventListener("DOMContentLoaded", function () {
  const favPanel = document.querySelector("#favorite-notifications"); // 收藏通知面板
  const favBadge = document.getElementById("badge-fav");              // 收藏通知徽章
  const markBtn = document.getElementById("markAllRead");             // 全部已讀按鈕

  /* ========== 工具函式 ========== */
  function countUnread(root) {
    return root ? root.querySelectorAll(".notification-unread").length : 0;
  }

  function setBadge(badge, n) {
    if (!badge) return;
    badge.textContent = n;
    if (n > 0) {
      badge.classList.remove("d-none");
      badge.classList.add("bg-danger");
    } else {
      badge.classList.add("d-none");
      badge.classList.remove("bg-danger");
    }
  }

  function updateFavCount() {
    setBadge(favBadge, countUnread(favPanel));
  }

  /* ========== 功能一：載入歷史收藏通知 ========== */
  function loadFavHistory() {
    csrfFetch(`/api/notifications/my`)
      .then(res => {
        if (!res.ok) throw new Error("無法載入收藏通知");
        return res.json();
      })
      .then(list => {
        favPanel.innerHTML = ""; // 清空舊內容
        if (!list || list.length === 0) {
          favPanel.innerHTML = `<p class="text-muted">目前沒有收藏通知</p>`;
          updateFavCount();
          return;
        }

        list.forEach(item => {
          const card = document.createElement("div");
          card.className = `card mb-3 shadow-sm border-0 ${item.readStatus ? "notification-read" : "notification-unread"}`;
          card.dataset.annId = item.favoriteAnnouncementId; // 後端通知 ID
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
        updateFavCount();
      })
      .catch(err => console.error("載入收藏通知失敗:", err));
  }

  /* ========== 功能二：單筆設為已讀 ========== */
  favPanel.addEventListener("click", function (e) {
    const card = e.target.closest(".card.notification-unread");
    if (!card) return;
    const annId = card.dataset.annId;
    csrfFetch(`/api/notifications/${annId}/read`, { method: "POST" })
      .then(res => {
        if (!res.ok) throw new Error("標記失敗");
        card.classList.replace("notification-unread", "notification-read");
        updateFavCount();
      })
      .catch(err => console.error("標記已讀失敗:", err));
  });

  /* ========== 功能三：全部設為已讀 ========== */
  if (markBtn) {
    markBtn.addEventListener("click", () => {
      csrfFetch(`/api/notifications/readAll`, { method: "POST" })
        .then(res => {
          if (!res.ok) throw new Error("全部標記失敗");
          favPanel.querySelectorAll(".notification-unread")
            .forEach(card => card.classList.replace("notification-unread", "notification-read"));
          updateFavCount();
        })
        .catch(err => console.error("全部已讀失敗:", err));
    });
  }

  /* ========== 初始化：載入歷史通知 ========== */
  loadFavHistory();
});