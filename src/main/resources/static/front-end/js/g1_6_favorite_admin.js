// 切換收藏用
(function() {
	// 抓取id = section-2
	var section = document.getElementById('section-2');
	if (!section) return;

	// 讀取data-toggle-url的值(即/favorite/toggle), 存到TOGGLE_URL, 供後續請求使用
	var TOGGLE_URL = section.getAttribute('data-toggle-url');

	// 綁定click事件
	$(document).on('click', '.btn_fav', function(e) {
		e.preventDefault(); // 去除標籤本身的預設行為
		var $btn = $(this);
		var memId = $btn.data('mem-id');
		var exhId = $btn.data('exh-id');
		 console.log(memId, exhId);

		$.ajax({
			url: TOGGLE_URL, // 即/favorite/toggle
			type: 'POST',
			data: { memId: memId, exhId: exhId },
			dataType: 'json',
			success: function(res) {
				// 確保回傳格式為json
				if (typeof res === "string") {
					try {
						res = JSON.parse(res);
					} catch (e) {
						console.log("回傳格式錯誤: ", res);
						return;
					}
				}
				
				if (res && res.success) {
					// 會員中心 - 收藏清單
					if ($btn.closest('.col-lg-4.col-md-6').length) {
						if (!res.favoriteStatus) {
							$btn.closest('.col-lg-4.col-md-6').fadeOut(200, function() {
								$(this).remove();
							});
						}
					}
					// 展覽頁 - 單一按鈕
					else {
						if (res.favoriteStatus) {
							$btn.addClass('active');
							$btn.find('i').addClass('icon-heart-filled');
							console.log("點即按鈕後, 切換 或 新增完成, 收藏狀態 = 1");
						} else {
							$btn.removeClass('active');
							$btn.find('i').removeClass('icon-heart-filled');
							console.log("點即按鈕後, 切換完成, 收藏狀態 = 0");
						}
					}
				} else {
					console.log((res && res.message) ? res.message : '更新失敗');
				}
			},
			error: function() {
				console.log('更新失敗');
			}
		});
	});
})();