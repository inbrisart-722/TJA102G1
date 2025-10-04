(function () {
  const KEY = 'exhibitor.sidebar.collapsed';
  const root = document.body; // ← 用 body 才會吃到 AdminLTE 的樣式

  function toggleSidebar() {
    const isDesktop = window.matchMedia('(min-width: 992px)').matches; // 對應 sidebar-expand-lg
    if (isDesktop) {
      root.classList.toggle('sidebar-collapse');
      localStorage.setItem(
        KEY,
        root.classList.contains('sidebar-collapse') ? '1' : '0'
      );
      // 確保桌面時不殘留手機的開啟狀態
      root.classList.remove('sidebar-open');
    } else {
      root.classList.toggle('sidebar-open');
      // 手機狀態不用記錄
    }
  }

  function restoreState() {
    const isDesktop = window.matchMedia('(min-width: 992px)').matches;
    if (!isDesktop) return;
    if (localStorage.getItem(KEY) === '1') {
      root.classList.add('sidebar-collapse');
    } else {
      root.classList.remove('sidebar-collapse');
    }
    // 切回桌面時移除手機狀態
    root.classList.remove('sidebar-open');
  }

  // 事件委派：header 晚載也能抓到
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('#sidebarToggle');
    if (!btn) return;
    e.preventDefault();
    toggleSidebar();
  });

  restoreState();
  window.matchMedia('(min-width: 992px)').addEventListener('change', restoreState);
  document.addEventListener('partial:loaded', restoreState);
})();
