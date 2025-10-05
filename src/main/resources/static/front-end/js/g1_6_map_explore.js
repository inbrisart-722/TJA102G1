// g1_6_map_explore.js
// 展覽資料渲染到地圖 : Marker + InfoBox

// 依平均分數顯示星星
function renderStars(avg, count) {
    let stars = "";
    const fullStars = Math.floor(avg);             // 整星數
    const hasHalf = avg - fullStars >= 0.1;       // 大於等於 0.1 算半星
    const emptyStars = 5 - fullStars - (hasHalf ? 1 : 0);

    // 整星
    for (let i = 0; i < fullStars; i++) {
        stars += `<i class="icon-star voted"></i>`;
    }

    // 半星
    if (hasHalf) {
        stars += `<i class="icon-star-half-alt voted"></i>`;
    }

    // 空星
    for (let i = 0; i < emptyStars; i++) {
        stars += `<i class="icon-star-empty"></i>`;
    }
	
	// 平均分數顯示到 1 位小數
	    const avgDisplay = avg ? avg.toFixed(1) : "0.0";

    return `${stars} <span><small> ${avgDisplay}</small> &nbsp;(${count || 0})</span>`;
}


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
					<img src="${item.photoPortrait}" alt="${item.exhibitionName}" style="width:260px; object-fit:cover"/>
					<h3>${item.exhibitionName}</h3>
					<span class="location">${item.location}</span>
					<span class="start_time">${formatDate(item.startTime)} ~ ${formatDate(item.endTime)}</span>
					<span class="rating">
						${renderStars(item.averageRatingScore, item.ratingCount)}
					</span>
					<div class="marker_tools">
					</div>
					<a href="/front-end/exhibitions/${item.exhibitionId}" class="btn_infobox">查看展覽</a>
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
