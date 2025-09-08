// 檢舉頁專用

document.addEventListener('DOMContentLoaded', () => {
    const sw = document.querySelector('.sidebar-wrapper');
    if (sw && OverlayScrollbarsGlobal?.OverlayScrollbars) {
        OverlayScrollbarsGlobal.OverlayScrollbars(sw, { scrollbars: { autoHide: 'leave' } });
    }
});

// ===== Demo 資料：與設計稿一致的兩筆 =====
const rawData = [
    {
        id: 'C00002',
        event: '2025蘑菇展《少年的奇幻漂流》',
        user: '王小華',
        content: '回覆：廣告連結進…',
        reason: '廣告推銷…，麻煩管理員處理',
        time: '2000/01/02 11:00',
        status: 'pending' // 待處理
    },
    {
        id: 'C00003',
        event: '2025-台灣燈會南投縣燈會公…',
        user: '林小美',
        content: '廣告連結請點…',
        reason: '廣告推銷…，麻煩管理員處理',
        time: '2000/01/02 11:00',
        status: 'done' // 已處理
    }
];

// ===== 狀態 =====
const state = {
    filter: 'all',  // all | pending | done
    query: '',
    page: 1,
    pageSize: 10,
    maxPage: 5      // 為了符合設計稿顯示 1~5
};

const $tbody = document.getElementById('tbody');
const $pager = document.getElementById('pager');
const $tabs = document.getElementById('tabs');
const $search = document.getElementById('search');

function normalize(s) { return (s || '').toLowerCase(); }

function getFiltered() {
    let list = rawData.slice();
    if (state.filter !== 'all') {
        list = list.filter(x => x.status === state.filter);
    }
    if (state.query) {
        const q = normalize(state.query);
        list = list.filter(x =>
            normalize(x.id).includes(q) ||
            normalize(x.event).includes(q) ||
            normalize(x.user).includes(q) ||
            normalize(x.content).includes(q) ||
            normalize(x.reason).includes(q) ||
            normalize(x.time).includes(q)
        );
    }
    return list;
}

function renderTable() {
    const list = getFiltered();
    // 只在第一頁顯示實際資料，其他頁做骨架列，貼近設計稿
    const rows = (state.page === 1) ? list : [];

    const html = rows.map(item => `
      <tr>
        <td>${item.id}</td>
        <td>${escapeHtml(item.event)}</td>
        <td>${escapeHtml(item.user)}</td>
        <td>${escapeHtml(item.content)}</td>
        <td>${escapeHtml(item.reason)}</td>
        <td>${item.time}</td>
        <td>${item.status === 'pending'
            ? '<span class="status-pending">待處理</span>'
            : '<span class="status-done">已處理</span>'}</td>
          <a href="report-detail.html" class="btn btn-ghost btn-sm">詳細</a>
        </td>
      </tr>
    `).join('');

    // 補骨架列到固定高度（10 列）
    const skeletonCount = Math.max(0, state.pageSize - rows.length);
    const skeletonHtml = Array.from({ length: skeletonCount }).map(() => `
      <tr><td colspan="8"><div class="skeleton-bar w-100"></div></td></tr>
    `).join('');

    $tbody.innerHTML = html + skeletonHtml;
}

function renderPager() {
    const pages = state.maxPage;
    const items = [];
    items.push(`<li class="page-item ${state.page === 1 ? 'disabled' : ''}">
      <a class="page-link" href="#" data-page="prev" aria-label="上一頁">«</a></li>`);
    for (let p = 1; p <= pages; p++) {
        items.push(`<li class="page-item ${p === state.page ? 'active' : ''}">
        <a class="page-link" href="#" data-page="${p}">${p}</a></li>`);
    }
    items.push(`<li class="page-item ${state.page === pages ? 'disabled' : ''}">
      <a class="page-link" href="#" data-page="next" aria-label="下一頁">»</a></li>`);
    $pager.innerHTML = items.join('');
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, m => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[m]));
}

// 事件：分頁籤
$tabs.addEventListener('click', (e) => {
    const btn = e.target.closest('[data-filter]');
    if (!btn) return;
    $tabs.querySelectorAll('.nav-link').forEach(x => x.classList.remove('active'));
    btn.classList.add('active');
    state.filter = btn.getAttribute('data-filter');
    state.page = 1;
    renderTable();
    renderPager();
});

// 事件：搜尋
$search.addEventListener('input', () => {
    state.query = $search.value.trim();
    state.page = 1;
    renderTable();
    renderPager();
});

// 事件：分頁
$pager.addEventListener('click', (e) => {
    const a = e.target.closest('a[data-page]');
    if (!a) return;
    e.preventDefault();
    const tag = a.getAttribute('data-page');
    if (tag === 'prev' && state.page > 1) state.page--;
    else if (tag === 'next' && state.page < state.maxPage) state.page++;
    else if (!isNaN(Number(tag))) state.page = Number(tag);
    renderTable();
    renderPager();
});

// 初始渲染
renderTable();
renderPager();