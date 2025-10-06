// map_full_page.js
// 建立地圖、使用者定位、地圖樣式控制

// 預設中心點常數 (緯育)
const DEFAULT_CENTER = { lat: 25.05213085756364, lng: 121.54325832447591 };

// 全域變數宣告
var mapObject; // 存放 Google Map 物件

// Google Maps 初始化
function initMap() {
	var mapOptions = {
		zoom: 17, // 地圖縮放等級(數字越大越近)
		center: new google.maps.LatLng(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng),
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

	// 建立 返回地圖 X 按鈕
	  const exitBtn = document.createElement("button");
	  exitBtn.id = "exit-streetview";
	  exitBtn.innerHTML = "✕";
	  Object.assign(exitBtn.style, {
	    display: "none",
	    position: "absolute",
		top: "90px",
		right: "30px",
		left: "auto",
	    zIndex: "9999",
	    width: "36px",
	    height: "36px",
	    lineHeight: "32px",
	    textAlign: "center",
	    fontSize: "20px",
	    fontWeight: "700",
	    color: "#333",
	    background: "#fff",
	    border: "1px solid #ccc",
	    borderRadius: "50%",
	    boxShadow: "0 2px 6px rgba(0,0,0,0.25)",
	    cursor: "pointer",
	  });
	  document.body.appendChild(exitBtn);
	
	// 監聽 Street View 狀態
	  const streetView = mapObject.getStreetView();
	  streetView.addListener("visible_changed", () => {
	    if (streetView.getVisible()) {
//	      console.log("進入街景模式");
			exitBtn.style.display = "block";
	    } else {
//	      console.log("離開街景模式，恢復地圖顯示");
			exitBtn.style.display = "none";
	      // 回到預設中心點
	      mapObject.setCenter(new google.maps.LatLng(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng));
	      mapObject.setZoom(17);
	    }
	  });
	  
	  // 點擊 X → 退出街景並重設地圖
	  exitBtn.addEventListener("click", () => {
	    streetView.setVisible(false);
	    mapObject.setCenter(new google.maps.LatLng(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng));
	    mapObject.setZoom(17);
	  });
	
	// 放置地圖中心點標記
	new google.maps.Marker({
		position: DEFAULT_CENTER,
		map: mapObject,
		title: "中心點",
	});

	// 直接載入展覽資料（1 公里範圍）
	loadNearbyExhibitions(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng, 1);

	//	// 取得使用者座標
	//	if (navigator.geolocation) {
	//		navigator.geolocation.getCurrentPosition(
	//			(position) => {
	//				const pos = {
	//					lat: position.coords.latitude,
	//					lng: position.coords.longitude,
	//				};
	//				console.log("定位結果: ", pos.lat, pos.lng);
	//				console.log("定位精準度(公尺): ", position.coords.accuracy);
	//
	//				mapObject.setCenter(pos);
	//
	//				// 使用者位置 marker
	//				new google.maps.Marker({
	//					position: pos,
	//					map: mapObject,
	//					title: "你在這裡",
	//					icon: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
	//				});
	//
	//				// 呼叫 API (g1_6_map_explore.js)
	//				loadNearbyExhibitions(pos.lat, pos.lng, 1);
	//			},
	//			(error) => {
	//				console.warn("定位失敗: ", error.message);
	//				// 如果定位失敗, 至少呼叫 API 以台北101為中心
	//				loadNearbyExhibitions(25.034199581347696, 121.56456035767059, 1);
	//			},
	//			{
	//				enableHighAccuracy: true, // 優先使用 GPS 或 Wi-Fi, 非 IP
	//				timeout: 10000,           // 最長等待 10 秒
	//				maximumAge: 0             // 不要用快取
	//			}
	//		);
	//	} else {
	//		console.warn("瀏覽器不支援 Geolocation API");
	//		loadNearbyExhibitions(25.034199581347696, 121.56456035767059, 1);
	//	}


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

