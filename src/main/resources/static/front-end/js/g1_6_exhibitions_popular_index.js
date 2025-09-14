/* 首頁 - 熱門展覽區 */

document.addEventListener("DOMContentLoaded", () => {
	// 抓熱門展覽輪播區, 即id=popularExhibitionsCarousel
	var $carousel = $("#popularExhibitionsCarousel");

	// jQuery 輪播效果(Owl Carousel)設定
	$carousel.owlCarousel({
	  items: 3,		// 一次顯示3個
	  margin: 10,
	  nav: true,	// 顯示左右箭頭
	  dots: false	// 不顯示下方小點點
	});

	// 後端API抓top5
	fetch("/api/exhibitions/popular/topN")
	  .then(res => res.json()) // 轉成 JSON
	  .then(data => {
		console.log(data);
		
		// 若後端API抓不到資料, 前台顯示無資料結果
		if (!data || data.length === 0) {
		  $carousel.append(`<p>目前沒有熱門展覽資料</p>`);
		  return;
		}
		
		// 若後端API抓到資料, 存入card
	    data.forEach(exh => {
	      var card = `
			<div class="item">
				<div class="tour_container">
					<div class="img_container">
						<a href="/front-end/exhibitions/${exh.exhibitionId}">
							<img src="img/0_exhibition/ChatGPT_exhibition_1.png" 
							class="img-fluid" alt="${exh.exhibitionName}">
							<div class="short_info">
								<span class="price">
							</div>
						</a>
					</div>
					<div class="tour_title"><h3>${exh.exhibitionName}</h3></div>
				</div>
			</div>`;
		
		  // 把card加入carousel (因應前端模板寫法 ,這裡是用Owl官方的API)
	      $carousel.trigger("add.owl.carousel", [$(card)]);
	    });
		
		
		// 更新carousel(讓加入的卡片顯示)
		$carousel.trigger("refresh.owl.carousel");
	  });

});

