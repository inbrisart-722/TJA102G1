// summernote_edit.js

$(document).ready(function () {
  $('#summernote').summernote({
    placeholder: '請輸入公告內容...',
    tabsize: 2,
    height: 200,
    lang: 'zh-TW'
  });

  // 表單送出時處理
  $('form').on('submit', function () {
    let content = $('#summernote').val();

    // 去掉 HTML 標籤 & 空白
    let plainText = content.replace(/<[^>]*>/g, '').trim();

    if (!plainText) {
      $('#summernote').val(''); // 讓後端看到真的空字串
    }
  });
});