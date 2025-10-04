// 搜尋按鈕錯誤驗證提示
document.addEventListener("DOMContentLoaded", function () {
  const searchForm = document.getElementById("searchForm");
  const keywordInput = document.getElementById("keywordInput");
  const keywordError = document.getElementById("keywordError");

  if (searchForm) {
    searchForm.addEventListener("submit", function (e) {
      const keyword = keywordInput.value.trim();

      // 顯示錯誤提示
      if (!keyword) {
        e.preventDefault();
        keywordError.style.display = "block";
        return;
      }

      // 隱藏錯誤提示
      keywordError.style.display = "none";
    });
  }
});

