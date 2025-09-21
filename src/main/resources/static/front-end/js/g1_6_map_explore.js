// g1_6_map_explore.js
// 展覽資料渲染到地圖 : Marker + InfoBox

var activeInfoWindow; // 追蹤目前打開的 InfoWindow

function loadNearbyExhibitions(lat, lng, radius) {
	fetch(`/api/exhibitions/nearby?lat=${lat}&lng=${lng}&radius=${radius}`)
		.then(response => response.json())
		.then(data => {
			// 測試用：印出回傳數量
			console.log("回傳展覽數:", data.length);

			// 畫出查詢半徑的圓形提示
			new google.maps.Circle({
				strokeColor: "#FF0000",
				strokeOpacity: 0.8,
				strokeWeight: 1,
				fillColor: "#FF0000",
				fillOpacity: 0,
				map: mapObject,
				center: { lat: lat, lng: lng }, // 以查詢中心為圓心
				radius: radius * 1000 // API 傳進來的 km → 轉換成公尺
			});

			// 照舊放 marker
			data.forEach(item => {
				const marker = new google.maps.Marker({
					position: { lat: item.latitude, lng: item.longitude },
					map: mapObject,
					title: item.exhibitionName
				});

				// InfoBox 內容（展覽卡片）
				const content = `
				<div class="marker_info" id="marker_info">
					<img src="img/0_exhibition/ChatGPT_exhibition_4.png" alt="${item.exhibitionName}" style="width:260px; object-fit:cover"/>
					<h3>${item.exhibitionName}</h3>
					<span class="location">${item.location}</span>
					<span class="start_time">${formatDate(item.startTime)} ~ ${formatDate(item.endTime)}</span>
					<span class="rating">
						<i class="icon-star voted "></i>
						<i class="icon-star voted"></i>
						<i class="icon-star-half-alt voted"></i>
						<i class="icon-star-empty"></i>
						<i class="icon-star-empty"></i>
						(${item.ratingCount})
					</span>
					<div class="marker_tools">
					</div>
					<a href="/exhibitions/${item.exhibitionId}" class="btn_infobox">查看展覽</a>
				</div>
				`;

				const infoWindow = new google.maps.InfoWindow({
					content: content,
				});

				// 點擊 Marker 開啟 InfoWindow
				marker.addListener("click", () => {
					// 關閉舊的 InfoWindow
					if (activeInfoWindow) {
						activeInfoWindow.close();
					}
					// 開啟新的
					infoWindow.open(mapObject, marker);
					activeInfoWindow = infoWindow;
				});
			});

			// 點擊地圖空白處, 關閉 InfoWindow
			mapObject.addListener("click", () => {
				if (activeInfoWindow) {
					activeInfoWindow.close();
					activeInfoWindow = null;
				}
			});
		})
		.catch(err => console.error("載入失敗:", err));
}

function formatDate(dateStr) {
	const date = new Date(dateStr);
	return date.toLocaleDateString("zh-TW");
}
