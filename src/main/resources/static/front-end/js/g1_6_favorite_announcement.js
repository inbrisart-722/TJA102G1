// g1_6_favorite_announcement.js
// <!-- 收藏、通知頁用 -->
document.addEventListener("DOMContentLoaded", function () {
  /* ===============================
   * 節點取得：右側三個面板、左側徽章、常用元素
   * =============================== */
  const panels = {
    order: document.querySelector("#order-notifications"), // 訂單通知面板
    fav: document.querySelector("#favorite-notifications"), // 收藏展覽通知面板
    platform: document.querySelector("#platform-notifications"), // 平台公告面板
  };
  const badges = {
    order: document.getElementById("badge-order"), // 左欄「訂單通知」未讀徽章
    fav: document.getElementById("badge-fav"), // 左欄「收藏展覽通知」未讀徽章
    platform: document.getElementById("badge-platform"), // 左欄「平台公告」未讀徽章
  };
  const faq = document.getElementById("faq"); // 右側通知容器（用於事件委派）
  const navLinks = document.querySelectorAll("#cat_nav a"); // 左欄三個分類連結
  const allPanels = document.querySelectorAll("#faq .notification-section"); // 三個面板的集合
  const markBtn = document.getElementById("markAllRead"); // 「全部標示為已讀」按鈕
  const titleEl = document.getElementById("notifTitle"); // 右側標題（可選）

  /* ===============================
   * 工具函式：計數與徽章顯示
   * =============================== */

  // 回傳某個面板底下「未讀」卡片的數量
  function countUnread(root) {
    return root ? root.querySelectorAll(".notification-unread").length : 0;
  }

  // 設定左欄徽章數字與顯示邏輯：
  // n > 0 → 顯示（紅色/危險色）；n = 0 → 隱藏（加 d-none）
  function setBadge(badge, n) {
    if (!badge) return;
    badge.textContent = n;

    if (n > 0) {
      badge.classList.remove("d-none");
      badge.classList.add("bg-danger");
      badge.classList.remove("bg-secondary");
    } else {
      badge.classList.add("d-none");
      badge.classList.remove("bg-danger");
      badge.classList.add("bg-secondary");
    }
  }

  // 重新計算三個面板的未讀數，並同步更新左欄三個徽章
  function updateCounts() {
    setBadge(badges.order, countUnread(panels.order));
    setBadge(badges.fav, countUnread(panels.fav));
    setBadge(badges.platform, countUnread(panels.platform));
  }

  /* ===============================
   * 功能一：點擊單一通知卡片 → 設為已讀（事件委派）
   * =============================== */
  faq.addEventListener("click", function (e) {
    const card = e.target.closest(".card");
    if (!card) return;

    // 若卡片內有「前往查看」等按鈕要保留原行為，可排除：
    // if (e.target.closest('a.btn, button')) return;

    if (card.classList.contains("notification-unread")) {
      card.classList.replace("notification-unread", "notification-read"); // 切換為已讀樣式
      updateCounts(); // 更新左欄未讀數
    }
  });

  /* ===============================
   * 功能二：aside 分類切換（左欄點擊 → 切換右側面板）
   * =============================== */
  navLinks.forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault();
      const targetSel = this.dataset.target; // 目標面板選擇器（例如 "#order-notifications"）
      if (!targetSel) return;

      // 左欄 active 樣式切換
      navLinks.forEach((a) => a.classList.remove("active"));
      this.classList.add("active");

      // 隱藏所有面板，僅顯示目標面板
      allPanels.forEach((sec) => sec.classList.add("d-none"));
      const panel = document.querySelector(targetSel);
      if (panel) panel.classList.remove("d-none");

      // （可選）同步右側標題：若不需要可維持註解
      // if (titleEl) titleEl.textContent = this.textContent.trim();

      // 切換面板後保險再更新一次徽章（雖然徽章是全域計）
      updateCounts();
    });
  });

  /* ===============================
   * 功能三：「全部標示為已讀」（僅針對當前顯示中的面板）
   * =============================== */
  if (markBtn) {
    markBtn.addEventListener("click", function () {
      // 找出目前可見的面板
      const visiblePanel = Array.from(allPanels).find(
        (sec) => !sec.classList.contains("d-none")
      );
      if (!visiblePanel) return;

      // 將該面板中的「未讀」全部改為「已讀」
      visiblePanel
        .querySelectorAll(".notification-unread")
        .forEach((card) =>
          card.classList.replace("notification-unread", "notification-read")
        );

      // 更新左欄徽章數
      updateCounts();
    });
  }

  /* ===============================
   * 初始化：載入後先計一次未讀數，更新徽章
   * =============================== */
  updateCounts();
});
