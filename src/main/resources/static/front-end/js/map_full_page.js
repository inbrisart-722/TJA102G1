// map_full_page.js
// 建立地圖、使用者定位、地圖樣式控制

// 全域變數宣告
var mapObject; // 存放 Google Map 物件


//var mapObject, // 存放 Google Map 物件
//	markers = [], // 存 marker (保留後續可能會優化 UI)
//	markersData = { // 假資料 (後續改成 API 抓資料)
//		pin: [
//			{
//				name: "test1",
//				location_latitude: 25.052283489684932,
//				location_longitude: 121.543242167044,
//				map_image_url: "/front-end/img/thumb_map_1.jpg",
//				name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
//				get_directions_start_address: "",
//				phone: "04-2339-1141",
//				url_point: "/front-end/exhibitions.html",
//			},
//			{
//				name: "test2",
//				location_latitude: 25.052322367622292,
//				location_longitude: 121.54133243435263,
//				map_image_url: "img/thumb_map_1.jpg",
//				name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
//				get_directions_start_address: "",
//				phone: "04-2339-1141",
//				url_point: "/front-end/exhibitions.html",
//			},
//		],
//	};


// Google Maps 初始化
function initMap() {
	var mapOptions = {
		zoom: 17, // 地圖縮放等級(數字越大越近)
		center: new google.maps.LatLng(25.034199581347696, 121.56456035767059), // 初始地圖中心點(台北101)
		mapTypeId: google.maps.MapTypeId.ROADMAP, // 地圖類型, 預設一般街道圖

		// 地圖控制選項
		mapTypeControl: false,
		mapTypeControlOptions: { style: google.maps.MapTypeControlStyle.DROPDOWN_MENU, position: google.maps.ControlPosition.LEFT_CENTER, },

		panControl: false,
		panControlOptions: { position: google.maps.ControlPosition.TOP_RIGHT, },

		zoomControl: true,
		zoomControlOptions: { style: google.maps.ZoomControlStyle.LARGE, position: google.maps.ControlPosition.LEFT_CENTER, },

		scrollwheel: true, // 允許滑鼠滾輪縮放
		gestureHandling: "auto",  // 手勢互動方式(auto | greedy | cooperative | none)

		scaleControl: false,
		scaleControlOptions: { position: google.maps.ControlPosition.LEFT_CENTER, },

		streetViewControl: true,
		streetViewControlOptions: { position: google.maps.ControlPosition.LEFT_CENTER, },

		// 地圖樣式設定, 高速公路、醫院、公司名稱等 隱藏
		styles: [
			{ featureType: "administrative.country", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "administrative.province", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "administrative.locality", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "administrative.neighborhood", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "administrative.land_parcel", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "landscape.man_made", elementType: "all", stylers: [{ visibility: "simplified", },], },
			{ featureType: "landscape.natural.landcover", elementType: "all", stylers: [{ visibility: "on", },], },
			{ featureType: "landscape.natural.terrain", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.attraction", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.business", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.government", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.medical", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.park", elementType: "all", stylers: [{ visibility: "on", },], },
			{ featureType: "poi.park", elementType: "labels", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.place_of_worship", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.school", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "poi.sports_complex", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "road.highway", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "road.highway", elementType: "labels", stylers: [{ visibility: "off", },], },
			{ featureType: "road.highway.controlled_access", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "road.arterial", elementType: "all", stylers: [{ visibility: "simplified", },], },
			{ featureType: "road.local", elementType: "all", stylers: [{ visibility: "simplified", },], },
			{ featureType: "transit.line", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "transit.station.airport", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "transit.station.bus", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "transit.station.rail", elementType: "all", stylers: [{ visibility: "off", },], },
			{ featureType: "water", elementType: "all", stylers: [{ visibility: "on", },], },
			{ featureType: "water", elementType: "labels", stylers: [{ visibility: "off", },], },
		],
	};

	// 建立地圖
	mapObject = new google.maps.Map(document.getElementById("map"), mapOptions);

	// 取得使用者座標
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(
			(position) => {
				const pos = {
					lat: position.coords.latitude,
					lng: position.coords.longitude,
				};
				console.log("定位結果: ", pos.lat, pos.lng);
				console.log("定位精準度(公尺): ", position.coords.accuracy);

				mapObject.setCenter(pos);

				// 使用者位置 marker
				new google.maps.Marker({
					position: pos,
					map: mapObject,
					title: "你在這裡",
					icon: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
				});

				// 呼叫 API (g1_6_map_explore.js)
				loadNearbyExhibitions(pos.lat, pos.lng, 1);
			},
			(error) => {
				console.warn("定位失敗: ", error.message);
				// 如果定位失敗, 至少呼叫 API 以台北101為中心
				loadNearbyExhibitions(25.034199581347696, 121.56456035767059, 1);
			},
			{
				enableHighAccuracy: true, // 優先使用 GPS 或 Wi-Fi, 非 IP
				timeout: 10000,           // 最長等待 10 秒
				maximumAge: 0             // 不要用快取
			}
		);
	} else {
		console.warn("瀏覽器不支援 Geolocation API");
		loadNearbyExhibitions(25.034199581347696, 121.56456035767059, 1);
	}
}

//
//// 圖片在這裡=====================================================================
//// 放置 Marker
//for (var key in markersData) {
//	markersData[key].forEach(function(item) {
//		var marker = new google.maps.Marker({
//			position: new google.maps.LatLng(
//				item.location_latitude,
//				item.location_longitude
//			),
//			map: mapObject,
//			title: item.name_point // 預設紅色水滴型 Marker，hover 會顯示標題
//		});
//
//		if (typeof markers[key] === "undefined") markers[key] = [];
//		markers[key].push(marker);
//
//		// Marker 點擊事件
//		google.maps.event.addListener(marker, "click", function() {
//			closeInfoBox();
//			getInfoBox(item).open(mapObject, this);
//			mapObject.setCenter(
//				new google.maps.LatLng(item.location_latitude, item.location_longitude)
//			);
//		});
//	});
//}
//
//
//// 關閉 InfoBox
//function closeInfoBox() {
//	document.querySelectorAll("div.infoBox").forEach(function(el) {
//		el.remove();
//	});
//}
//
//// 建立 InfoBox
//function getInfoBox(item) {
//	return new InfoBox({
//		content:
//			'<div class="marker_info" id="marker_info">' +
//			'<img src="' +
//			item.map_image_url +
//			'" alt="Image"/>' +
//			"<h3>" +
//			item.name_point +
//			"</h3>" +
//			// '<span>'+ item.description_point +'</span>' +
//			'<span class="start_time">2025/08/01 ~ 2025/08/01</span>' +
//			'<span class="rating"><i class="icon-star voted "></i><i class="icon-star voted"></i><i class="icon-star-half-alt voted"></i><i class="icon-star-empty"></i><i class="icon-star-empty"></i>(22)</span>' +
//			'<div class="marker_tools">' +
//			'<form action="https://maps.google.com/maps" method="get" target="_blank" style="display:inline-block""><input name="saddr" value="' +
//			item.get_directions_start_address +
//			'" type="hidden"><input type="hidden" name="daddr" value="' +
//			item.location_latitude +
//			"," +
//			item.location_longitude +
//			'"><button type="submit" value="Get directions" class="btn_infobox_get_directions">Directions</button></form>' +
//			'<a href="tel://' +
//			item.phone +
//			'" class="btn_infobox_phone">' +
//			item.phone +
//			"</a>" +
//			"</div>" +
//			'<a href="' +
//			item.url_point +
//			'" class="btn_infobox">查看展覽</a>' +
//			"</div>",
//		disableAutoPan: false,
//		maxWidth: 0,
//		pixelOffset: new google.maps.Size(10, 125),
//		closeBoxMargin: "5px -20px 2px 2px",
//		closeBoxURL: "img/close_infobox.png",
//		isHidden: false,
//		alignBottom: true,
//		pane: "floatPane",
//		enableEventPropagation: true,
//	});
//}

