(function (A) {
  if (!Array.prototype.forEach)
    A.forEach =
      A.forEach ||
      function (action, that) {
        for (var i = 0, l = this.length; i < l; i++)
          if (i in this) action.call(that, this[i], i, this);
      };
})(Array.prototype);

// 卡片在這裡=====================================================================
var mapObject,
  markers = [],
  markersData = {
    pin: [
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.873792,
        location_longitude: 2.295028,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.846222,
        location_longitude: 2.346414,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "感都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.865633,
        location_longitude: 2.321236,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.854183,
        location_longitude: 2.354808,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.863893,
        location_longitude: 2.342348,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.860642,
        location_longitude: 2.352245,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.85837,
        location_longitude: 2.294481,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.837273,
        location_longitude: 2.335387,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.860819,
        location_longitude: 2.354507,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.853798,
        location_longitude: 2.333328,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.86288,
        location_longitude: 2.287205,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.865784,
        location_longitude: 2.307314,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.852729,
        location_longitude: 2.350564,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
      {
        name: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        location_latitude: 48.870587,
        location_longitude: 2.318943,
        map_image_url: "img/thumb_map_1.jpg",
        name_point: "都市敘事：以鏡頭書寫在城市縫隙間的故事與記憶",
        get_directions_start_address: "",
        phone: "04-2339-1141",
        url_point: "single_tour.html",
      },
    ],
  };

var mapOptions = {
  zoom: 13,
  center: new google.maps.LatLng(48.865633, 2.321236),
  mapTypeId: google.maps.MapTypeId.ROADMAP,

  mapTypeControl: false,
  mapTypeControlOptions: {
    style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
    position: google.maps.ControlPosition.LEFT_CENTER,
  },
  panControl: false,
  panControlOptions: {
    position: google.maps.ControlPosition.TOP_RIGHT,
  },
  zoomControl: true,
  zoomControlOptions: {
    style: google.maps.ZoomControlStyle.LARGE,
    position: google.maps.ControlPosition.LEFT_CENTER,
  },
  scrollwheel: false,
  scaleControl: false,
  scaleControlOptions: {
    position: google.maps.ControlPosition.LEFT_CENTER,
  },
  streetViewControl: true,
  streetViewControlOptions: {
    position: google.maps.ControlPosition.LEFT_CENTER,
  },
  styles: [
    {
      featureType: "administrative.country",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "administrative.province",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "administrative.locality",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "administrative.neighborhood",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "administrative.land_parcel",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "landscape.man_made",
      elementType: "all",
      stylers: [
        {
          visibility: "simplified",
        },
      ],
    },
    {
      featureType: "landscape.natural.landcover",
      elementType: "all",
      stylers: [
        {
          visibility: "on",
        },
      ],
    },
    {
      featureType: "landscape.natural.terrain",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.attraction",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.business",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.government",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.medical",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.park",
      elementType: "all",
      stylers: [
        {
          visibility: "on",
        },
      ],
    },
    {
      featureType: "poi.park",
      elementType: "labels",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.place_of_worship",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.school",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "poi.sports_complex",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "road.highway",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "road.highway",
      elementType: "labels",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "road.highway.controlled_access",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "road.arterial",
      elementType: "all",
      stylers: [
        {
          visibility: "simplified",
        },
      ],
    },
    {
      featureType: "road.local",
      elementType: "all",
      stylers: [
        {
          visibility: "simplified",
        },
      ],
    },
    {
      featureType: "transit.line",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "transit.station.airport",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "transit.station.bus",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "transit.station.rail",
      elementType: "all",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
    {
      featureType: "water",
      elementType: "all",
      stylers: [
        {
          visibility: "on",
        },
      ],
    },
    {
      featureType: "water",
      elementType: "labels",
      stylers: [
        {
          visibility: "off",
        },
      ],
    },
  ],
};

// 圖片在這裡=====================================================================
var marker;
mapObject = new google.maps.Map(document.getElementById("map"), mapOptions);
for (var key in markersData)
  markersData[key].forEach(function (item) {
    marker = new google.maps.Marker({
      position: new google.maps.LatLng(
        item.location_latitude,
        item.location_longitude
      ),
      map: mapObject,
      icon: "img/pins/" + key + ".png",
    });

    if ("undefined" === typeof markers[key]) markers[key] = [];
    markers[key].push(marker);
    google.maps.event.addListener(marker, "click", function () {
      closeInfoBox();
      getInfoBox(item).open(mapObject, this);
      mapObject.setCenter(
        new google.maps.LatLng(item.location_latitude, item.location_longitude)
      );
    });
  });

function hideAllMarkers() {
  for (var key in markers)
    markers[key].forEach(function (marker) {
      marker.setMap(null);
    });
}

function toggleMarkers(category) {
  hideAllMarkers();
  closeInfoBox();

  if ("undefined" === typeof markers[category]) return false;
  markers[category].forEach(function (marker) {
    marker.setMap(mapObject);
    marker.setAnimation(google.maps.Animation.DROP);
  });
}

function closeInfoBox() {
  $("div.infoBox").remove();
}

function getInfoBox(item) {
  return new InfoBox({
    content:
      '<div class="marker_info" id="marker_info">' +
      '<img src="' +
      item.map_image_url +
      '" alt="Image"/>' +
      "<h3>" +
      item.name_point +
      "</h3>" +
      // '<span>'+ item.description_point +'</span>' +
      '<span class="start_time">2025/08/01 ~ 2025/08/01</span>' +
      '<span class="rating"><i class="icon-star voted "></i><i class="icon-star voted"></i><i class="icon-star-half-alt voted"></i><i class="icon-star-empty"></i><i class="icon-star-empty"></i>(22)</span>' +
      '<div class="marker_tools">' +
      '<form action="https://maps.google.com/maps" method="get" target="_blank" style="display:inline-block""><input name="saddr" value="' +
      item.get_directions_start_address +
      '" type="hidden"><input type="hidden" name="daddr" value="' +
      item.location_latitude +
      "," +
      item.location_longitude +
      '"><button type="submit" value="Get directions" class="btn_infobox_get_directions">Directions</button></form>' +
      '<a href="tel://' +
      item.phone +
      '" class="btn_infobox_phone">' +
      item.phone +
      "</a>" +
      "</div>" +
      '<a href="' +
      item.url_point +
      '" class="btn_infobox">查看展覽</a>' +
      "</div>",
    disableAutoPan: false,
    maxWidth: 0,
    pixelOffset: new google.maps.Size(10, 125),
    closeBoxMargin: "5px -20px 2px 2px",
    closeBoxURL: "img/close_infobox.png",
    isHidden: false,
    alignBottom: true,
    pane: "floatPane",
    enableEventPropagation: true,
  });
}
