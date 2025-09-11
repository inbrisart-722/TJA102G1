// 頁面載入檢查是否已收藏
$(function () {
    var $btn = $("#favorite");
    var memId = $btn.data("mem-id"); // 會員 ID
    var exhId = $btn.data("exh-id"); // 展覽 ID

    // 呼叫後端檢查
    $.getJSON("/favorite/check", { memId: memId, exhId: exhId }, function (res) {
		 console.log("memId= " + memId + ", exhId= " + exhId);
        if (res.success && res.favoriteStatus) {
            $btn.addClass("active");
			console.log("載入頁面的初始收藏狀態 = 1");
        } else {
            $btn.removeClass("active");
			console.log("載入頁面的初始收藏狀態 = 0");
        }
    });
});

// 切換收藏用
(function() {
	// 抓取id = favorite
	var section = document.getElementById('favorite');
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