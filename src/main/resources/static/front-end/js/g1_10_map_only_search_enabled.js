document.addEventListener("DOMContentLoaded", function () {
  // 搜尋框點擊後滿版顯示（本來只有小 icon 會觸發）
  const el = document.querySelector("div.search-overlay-menu");
  const search_input = document.querySelector("#searchform input");
  const search_block_el = document.querySelector("#nav_icon_1");

  search_block_el.addEventListener("click", function () {
    el.classList.add("open");
    setTimeout(() => search_input.focus(), 50);
  });
  // search bar 也綁定事件去切換是否 sticky 的樣式
  const searchInput = document.getElementById("nav_search");
  const sentinel = document.createElement("div");
  sentinel.style.height = "1px";
  document.body.prepend(sentinel);

  const observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].boundingClientRect.y < 0) {
        searchInput.classList.add("is-sticky");
      } else {
        searchInput.classList.remove("is-sticky");
      }
    },
    { threshold: [1] }
  );

  observer.observe(sentinel);
});
