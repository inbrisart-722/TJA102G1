package com.eventra.linebot.util;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eventra.exhibition.model.ExhibitionLineBotCarouselDTO;
import com.eventra.order.model.OrderLineBotCarouselDTO;
import com.eventra.order.model.OrderStatus; // 依你的實際 package 調整
import com.eventra.order_item.model.OrderItemLineBotCarouselDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.QrCodeUtil;

public class LineBotFlexBuilder {

	private final ObjectMapper mapper = new ObjectMapper();
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm");

	// ---------------------------
	// 共用：把 contents 包成 reply JSON
	// ---------------------------
	public String wrapFlexReply(String replyToken, ObjectNode contents) {
		ObjectNode root = mapper.createObjectNode();
		root.put("replyToken", replyToken);

		ObjectNode flex = mapper.createObjectNode();
		flex.put("type", "flex");
		flex.put("altText", "Eventra");
		flex.set("contents", contents);

		ArrayNode messages = mapper.createArrayNode();
		messages.add(flex);
		root.set("messages", messages);

		try {
			return mapper.writeValueAsString(root);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// =========================================================
	// 展覽用：Carousel / Bubble
	// =========================================================

	public ObjectNode buildExhibitionCarousel(List<ExhibitionLineBotCarouselDTO> list, boolean hasNextPage,
			String action, String type, int nextPage) {
		ObjectNode carousel = mapper.createObjectNode();
		carousel.put("type", "carousel");
		ArrayNode contents = mapper.createArrayNode();

		for (ExhibitionLineBotCarouselDTO ex : list) {
			contents.add(buildExhibitionBubble(ex));
		}

		if (hasNextPage) {
			contents.add(buildSeeMoreBubble(action, type, nextPage));
		}

		carousel.set("contents", contents);
		return carousel;
	}

	private ObjectNode buildExhibitionBubble(ExhibitionLineBotCarouselDTO ex) {
		ObjectNode bubble = mapper.createObjectNode();
		bubble.put("type", "bubble");

		// hero
		ObjectNode hero = mapper.createObjectNode();
		hero.put("type", "image");
		hero.put("url", ex.getPhotoPortrait());
		hero.put("size", "full");
		hero.put("aspectRatio", "3:2");
		hero.put("aspectMode", "cover");
		ObjectNode heroAction = mapper.createObjectNode();
		heroAction.put("type", "uri");
		heroAction.put("label", "展覽頁");
		heroAction.put("uri", ex.getPageUrl());
		hero.set("action", heroAction);
		bubble.set("hero", hero);

		// body
		ObjectNode body = mapper.createObjectNode();
		body.put("type", "box");
		body.put("layout", "vertical");
		ArrayNode bodyContents = mapper.createArrayNode();

		ObjectNode title = mapper.createObjectNode();
		title.put("type", "text");
		title.put("text", ex.getExhibitionName());
		title.put("weight", "bold");
		title.put("size", "xl");
		title.put("margin", "sm");
		bodyContents.add(title);

		bodyContents.add(buildRatingBox(ex.getAverageRatingScore()));
		bodyContents.add(buildInfoRow("📍", ex.getLocation(), null));
		bodyContents.add(buildInfoRow("🕓", ex.getStartTime().format(dtf) + " ~", null));
		bodyContents.add(buildInfoRow("", ex.getEndTime().format(dtf), "24px"));

		body.set("contents", bodyContents);
		bubble.set("body", body);

		// footer
		ObjectNode footer = mapper.createObjectNode();
		footer.put("type", "box");
		footer.put("layout", "vertical");
		footer.put("flex", 0);
		footer.put("spacing", "xs");

		ArrayNode footerContents = mapper.createArrayNode();
		ObjectNode button = mapper.createObjectNode();
		button.put("type", "button");
		button.put("height", "sm");
		button.put("style", "link");
		ObjectNode action = mapper.createObjectNode();
		action.put("type", "uri");
		action.put("label", "前往展覽頁");
		action.put("uri", ex.getPageUrl());
		button.set("action", action);
		footerContents.add(button);

		footerContents.add(mapper.createObjectNode().put("type", "spacer"));
		footer.set("contents", footerContents);
		bubble.set("footer", footer);

		return bubble;
	}

	private ObjectNode buildRatingBox(Double scoreValue) {
		ObjectNode ratingBox = mapper.createObjectNode();
		ratingBox.put("type", "box");
		ratingBox.put("layout", "baseline");
		ratingBox.put("margin", "xxl");
		ArrayNode ratingContents = mapper.createArrayNode();

		int fullStars = scoreValue == null ? 0 : scoreValue.intValue();
		for (int i = 0; i < fullStars; i++)
			ratingContents.add(makeStar("gold"));
		for (int i = fullStars; i < 5; i++)
			ratingContents.add(makeStar("gray"));

		ObjectNode score = mapper.createObjectNode();
		score.put("type", "text");
		score.put("text", scoreValue == null ? "0.0" : String.format("%.1f", scoreValue));
		score.put("size", "sm");
		score.put("color", "#999999");
		score.put("flex", 0);
		score.put("margin", "md");
		ratingContents.add(score);

		ratingBox.set("contents", ratingContents);
		return ratingBox;
	}

	private ObjectNode makeStar(String color) {
		ObjectNode star = mapper.createObjectNode();
		star.put("type", "icon");
		star.put("size", "sm");
		if ("gold".equals(color)) {
			star.put("url", "https://scdn.line-apps.com/n/channel_devcenter/img/fx/review_gold_star_28.png");
		} else {
			star.put("url", "https://scdn.line-apps.com/n/channel_devcenter/img/fx/review_gray_star_28.png");
		}
		return star;
	}

	private ObjectNode buildInfoRow(String icon, String text, String offsetStart) {
		ObjectNode row = mapper.createObjectNode();
		row.put("type", "box");
		row.put("layout", "baseline");
		row.put("spacing", "sm");
		row.put("margin", "md");
		ArrayNode contents = mapper.createArrayNode();

		if (!icon.isEmpty()) {
			ObjectNode iconNode = mapper.createObjectNode();
			iconNode.put("type", "text");
			iconNode.put("text", icon);
			iconNode.put("size", "xl");
			iconNode.put("color", "#AAAAAA");
			contents.add(iconNode);
		}

		ObjectNode txt = mapper.createObjectNode();
		txt.put("type", "text");
		txt.put("text", text);
		txt.put("size", "sm");
		txt.put("color", "#666666");
		txt.put("flex", 10);
		txt.put("margin", "sm");
		txt.put("wrap", true);
		if (offsetStart != null) {
			txt.put("offsetStart", offsetStart);
		}
		contents.add(txt);

		row.set("contents", contents);
		return row;
	}

	// =========================================================
	// 訂單用：Carousel / Bubble
	// =========================================================

	public ObjectNode buildOrderCarousel(List<OrderLineBotCarouselDTO> orders, boolean hasNextPage, String action, // e.g.
																													// "search_orders"
			String type, // e.g. "paid"/"unpaid"，不需要可傳 null
			int nextPage) {
		ObjectNode carousel = mapper.createObjectNode();
		carousel.put("type", "carousel");
		ArrayNode contents = mapper.createArrayNode();

		for (OrderLineBotCarouselDTO o : orders) {
			contents.add(buildOrderBubble(o));
		}

		if (hasNextPage) {
			contents.add(
					buildSeeMoreBubble(action != null ? action : "search_orders", type != null ? type : "", nextPage));
		}

		carousel.set("contents", contents);
		return carousel;
	}

	private ObjectNode buildOrderBubble(OrderLineBotCarouselDTO o) {
		ObjectNode bubble = mapper.createObjectNode();
		bubble.put("type", "bubble");

		// hero：先放示意圖或品牌圖
		ObjectNode hero = mapper.createObjectNode();
		hero.put("type", "image");
		hero.put("url", "https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_3_movie.png");
		hero.put("size", "full");
		hero.put("aspectRatio", "16:9");
		hero.put("aspectMode", "cover");
		ObjectNode heroAction = mapper.createObjectNode();
		heroAction.put("type", "uri");
		heroAction.put("label", "Action");
		heroAction.put("uri", "https://linecorp.com/");
		hero.set("action", heroAction);
		bubble.set("hero", hero);

		// body
		ObjectNode body = mapper.createObjectNode();
		body.put("type", "box");
		body.put("layout", "vertical");
		body.put("spacing", "md");
		ArrayNode bodyContents = mapper.createArrayNode();

		ObjectNode title = mapper.createObjectNode();
		title.put("type", "text");
		title.put("text", "Eventra");
		title.put("weight", "bold");
		title.put("align", "center");
		bodyContents.add(title);

		bodyContents.add(mapper.createObjectNode().put("type", "separator").put("margin", "lg"));

		// 訂單狀態
		bodyContents.add(buildTwoColRow("訂單狀態", (o.getOrderStatus() != null ? o.getOrderStatus().toString() : "-"),
				"#AAAAAA", "#2F57E9FF", 11, 2));

		// 總金額
		bodyContents.add(buildTwoColRow("總金額", (o.getTotalAmount() != null ? String.valueOf(o.getTotalAmount()) : "-"),
				"#AAAAAA", "#666666", 11, 2));

		// 總數量
		bodyContents
				.add(buildTwoColRow("總數量", (o.getTotalQuantity() != null ? String.valueOf(o.getTotalQuantity()) : "-"),
						"#AAAAAA", "#666666", 11, 2));

		// 訂單 ID
		bodyContents.add(buildTwoColRow("訂單 ID", (o.getOrderUlid() != null ? o.getOrderUlid() : "-"), "#AAAAAA",
				"#666666", 1, 1));

		body.set("contents", bodyContents);
		bubble.set("body", body);

		// footer -> 只有 OrderStatus.已付款 的訂單才會有 QR Code 按鈕。
		if (o.getOrderStatus() == OrderStatus.已付款) {
			ObjectNode footer = mapper.createObjectNode();
			footer.put("type", "box");
			footer.put("layout", "horizontal");
			footer.put("flex", 1);

			ArrayNode footerContents = mapper.createArrayNode();
			ObjectNode button = mapper.createObjectNode();
			button.put("type", "button");

			ObjectNode action = mapper.createObjectNode();
			action.put("type", "postback");
			action.put("label", "取得入場 QR Code");
			action.put("data", "action=get_ticket_qr&orderUlid=" + o.getOrderUlid() + "&page=0");

			button.set("action", action);
			footerContents.add(button);

			footer.set("contents", footerContents);
			bubble.set("footer", footer);
		}

		return bubble;
	}

	// =========================================================
	// 訂單明細（票 qr code）用：Carousel / Bubble
	// =========================================================
	public ObjectNode buildOrderItemCarousel(List<OrderItemLineBotCarouselDTO> items, boolean hasNextPage,
			String action, // 建議固定 "get_ticket_qr"
			String orderUlid, // 查明細一定要帶 orderUlid
			int nextPage) {

		ObjectNode carousel = mapper.createObjectNode();
		carousel.put("type", "carousel");
		ArrayNode contents = mapper.createArrayNode();

		for (OrderItemLineBotCarouselDTO it : items) {
			contents.add(buildOrderItemBubble(it));
		}

		if (hasNextPage) {
			contents.add(
					buildSeeMoreBubbleWithQuery("action=" + action + "&orderId=" + orderUlid + "&page=" + nextPage));
		}

		carousel.set("contents", contents);
		return carousel;
	}

	private ObjectNode buildOrderItemBubble(OrderItemLineBotCarouselDTO it) {
	    ObjectNode bubble = mapper.createObjectNode();
	    bubble.put("type", "bubble");

	    // hero：展覽圖片
	    ObjectNode hero = mapper.createObjectNode();
	    hero.put("type", "image");
	    hero.put("url", it.getPhotoPortrait());
	    hero.put("size", "full");
	    hero.put("aspectRatio", "3:2");
	    hero.put("aspectMode", "cover");
	    ObjectNode heroAction = mapper.createObjectNode();
	    heroAction.put("type", "uri");
	    heroAction.put("label", "展覽頁");
	    heroAction.put("uri", it.getPageUrl());
	    hero.set("action", heroAction);
	    bubble.set("hero", hero);

	    // body
	    ObjectNode body = mapper.createObjectNode();
	    body.put("type", "box");
	    body.put("layout", "vertical");
	    body.put("spacing", "md");
	    ArrayNode bodyContents = mapper.createArrayNode();

	    // 標題（展覽名稱）
	    ObjectNode title = mapper.createObjectNode();
	    title.put("type", "text");
	    title.put("text", it.getExhibitionName());
	    title.put("weight", "bold");
	    title.put("size", "xl");
	    title.put("gravity", "center");
	    title.put("wrap", true);
	    bodyContents.add(title);

	    // 訂單明細 ID
	    ObjectNode orderItemRow = mapper.createObjectNode();
	    orderItemRow.put("type", "box");
	    orderItemRow.put("layout", "baseline");
	    orderItemRow.put("spacing", "sm");
	    ArrayNode orderItemRowContents = mapper.createArrayNode();

	    ObjectNode orderItemLabel = mapper.createObjectNode();
	    orderItemLabel.put("type", "text");
	    orderItemLabel.put("text", "訂單明細 ID");
	    orderItemLabel.put("size", "sm");
	    orderItemLabel.put("color", "#AAAAAA");
	    orderItemLabel.put("flex", 1);
	    orderItemRowContents.add(orderItemLabel);

	    ObjectNode orderItemValue = mapper.createObjectNode();
	    orderItemValue.put("type", "text");
	    orderItemValue.put("text", it.getOrderItemUlid() != null ? it.getOrderItemUlid() : "-");
	    orderItemValue.put("size", "sm");
	    orderItemValue.put("color", "#666666");
	    orderItemValue.put("flex", 1);
	    orderItemValue.put("wrap", true);
	    orderItemRowContents.add(orderItemValue);

	    orderItemRow.set("contents", orderItemRowContents);
	    bodyContents.add(orderItemRow);

	    // 票號
	    ObjectNode ticketRow = mapper.createObjectNode();
	    ticketRow.put("type", "box");
	    ticketRow.put("layout", "baseline");
	    ticketRow.put("spacing", "sm");
	    ArrayNode ticketRowContents = mapper.createArrayNode();

	    ObjectNode ticketLabel = mapper.createObjectNode();
	    ticketLabel.put("type", "text");
	    ticketLabel.put("text", "票號");
	    ticketLabel.put("size", "sm");
	    ticketLabel.put("color", "#AAAAAA");
	    ticketLabel.put("flex", 1);
	    ticketRowContents.add(ticketLabel);

	    ObjectNode ticketValue = mapper.createObjectNode();
	    ticketValue.put("type", "text");
	    ticketValue.put("text", it.getTicketCode() != null ? it.getTicketCode() : "-");
	    ticketValue.put("size", "sm");
	    ticketValue.put("color", "#666666");
	    ticketValue.put("flex", 1);
	    ticketValue.put("wrap", true);
	    ticketRowContents.add(ticketValue);

	    ticketRow.set("contents", ticketRowContents);
	    bodyContents.add(ticketRow);

	    // 開始時間
	    ObjectNode startRow = mapper.createObjectNode();
	    startRow.put("type", "box");
	    startRow.put("layout", "baseline");
	    startRow.put("spacing", "sm");
	    startRow.put("margin", "xxl");
	    ArrayNode startRowContents = mapper.createArrayNode();

	    ObjectNode startIcon = mapper.createObjectNode();
	    startIcon.put("type", "text");
	    startIcon.put("text", "🕓");
	    startIcon.put("size", "sm");
	    startIcon.put("color", "#AAAAAA");
	    startIcon.put("flex", 1);
	    startIcon.put("wrap", true);
	    startRowContents.add(startIcon);

	    ObjectNode startValue = mapper.createObjectNode();
	    startValue.put("type", "text");
	    startValue.put("text", it.getStartTime() != null ? it.getStartTime().format(dtf) + " ~" : "-");
	    startValue.put("size", "sm");
	    startValue.put("color", "#666666");
	    startValue.put("flex", 9);
	    startValue.put("wrap", true);
	    startRowContents.add(startValue);

	    startRow.set("contents", startRowContents);
	    bodyContents.add(startRow);

	    // 結束時間
	    ObjectNode endRow = mapper.createObjectNode();
	    endRow.put("type", "box");
	    endRow.put("layout", "baseline");
	    endRow.put("spacing", "sm");
	    ArrayNode endRowContents = mapper.createArrayNode();

	    ObjectNode endValue = mapper.createObjectNode();
	    endValue.put("type", "text");
	    endValue.put("text", it.getEndTime() != null ? it.getEndTime().format(dtf) : "-");
	    endValue.put("size", "sm");
	    endValue.put("color", "#666666");
	    endValue.put("flex", 9);
	    endValue.put("wrap", true);
	    endValue.put("offsetStart", "30px");
	    endRowContents.add(endValue);

	    endRow.set("contents", endRowContents);
	    bodyContents.add(endRow);

	    // 地點
	    ObjectNode locRow = mapper.createObjectNode();
	    locRow.put("type", "box");
	    locRow.put("layout", "baseline");
	    locRow.put("spacing", "sm");
	    locRow.put("margin", "md");
	    ArrayNode locRowContents = mapper.createArrayNode();

	    ObjectNode locIcon = mapper.createObjectNode();
	    locIcon.put("type", "text");
	    locIcon.put("text", "📍");
	    locIcon.put("size", "sm");
	    locIcon.put("color", "#AAAAAA");
	    locIcon.put("flex", 1);
	    locRowContents.add(locIcon);

	    ObjectNode locValue = mapper.createObjectNode();
	    locValue.put("type", "text");
	    locValue.put("text", it.getLocation() != null ? it.getLocation() : "-");
	    locValue.put("size", "sm");
	    locValue.put("color", "#666666");
	    locValue.put("flex", 9);
	    locValue.put("wrap", true);
	    locRowContents.add(locValue);

	    locRow.set("contents", locRowContents);
	    bodyContents.add(locRow);

	    // QR Code 區塊
	    ObjectNode qrBox = mapper.createObjectNode();
	    qrBox.put("type", "box");
	    qrBox.put("layout", "vertical");
	    qrBox.put("margin", "xxl");
	    ArrayNode qrContents = mapper.createArrayNode();

	    qrContents.add(mapper.createObjectNode().put("type", "spacer"));

	    ObjectNode qrImage = mapper.createObjectNode();
	    qrImage.put("type", "image");
	    // ?? 測試中
	    qrImage.put("url", "https://scdn.line-apps.com/n/channel_devcenter/img/fx/linecorp_code_withborder.png");
	    qrImage.put("size", "xl");
	    qrImage.put("aspectMode", "cover");
	    qrContents.add(qrImage);

	    ObjectNode qrHint = mapper.createObjectNode();
	    qrHint.put("type", "text");
	    qrHint.put("text", "於展覽期間出示此 QR code 即可入場！");
	    qrHint.put("size", "xs");
	    qrHint.put("color", "#AAAAAA");
	    qrHint.put("align", "center");
	    qrHint.put("gravity", "center");
	    qrHint.put("margin", "xxl");
	    qrHint.put("wrap", true);
	    qrContents.add(qrHint);

	    qrBox.set("contents", qrContents);
	    bodyContents.add(qrBox);

	    body.set("contents", bodyContents);
	    bubble.set("body", body);

	    return bubble;
	}

	
	private ObjectNode buildSeeMoreBubbleWithQuery(String query) {
		ObjectNode bubble = mapper.createObjectNode();
		bubble.put("type", "bubble");

		ObjectNode body = mapper.createObjectNode();
		body.put("type", "box");
		body.put("layout", "vertical");
		body.put("spacing", "sm");

		ArrayNode contents = mapper.createArrayNode();

		ObjectNode button = mapper.createObjectNode();
		button.put("type", "button");
		button.put("flex", 1);
		button.put("margin", "xxl");
		button.put("height", "md");
		button.put("style", "primary");
		button.put("gravity", "center");

		ObjectNode postback = mapper.createObjectNode();
		postback.put("type", "postback");
		postback.put("label", "查看更多");
		postback.put("data", query); // 直接用你傳進來的 query

		button.set("action", postback);
		contents.add(button);

		body.set("contents", contents);
		bubble.set("body", body);
		return bubble;
	}

	// ---------------------------
	// 共用：雙欄 baseline row
	// ---------------------------
	private ObjectNode buildTwoColRow(String left, String right, String leftColor, String rightColor, Integer leftFlex,
			Integer rightFlex) {
		ObjectNode row = mapper.createObjectNode();
		row.put("type", "box");
		row.put("layout", "baseline");
		row.put("spacing", "sm");

		ArrayNode contents = mapper.createArrayNode();

		ObjectNode l = mapper.createObjectNode();
		l.put("type", "text");
		l.put("text", left);
		l.put("size", "sm");
		l.put("color", leftColor);
		l.put("flex", leftFlex);
		contents.add(l);

		ObjectNode r = mapper.createObjectNode();
		r.put("type", "text");
		r.put("text", right);
		r.put("size", "sm");
		r.put("color", rightColor);
		r.put("flex", rightFlex);
		r.put("wrap", true);
		contents.add(r);

		row.set("contents", contents);
		return row;
	}

	// ---------------------------
	// 共用：「查看更多」Bubble
	// ---------------------------
	private ObjectNode buildSeeMoreBubble(String action, String type, int page) {
		ObjectNode bubble = mapper.createObjectNode();
		bubble.put("type", "bubble");

		ObjectNode body = mapper.createObjectNode();
		body.put("type", "box");
		body.put("layout", "vertical");
		body.put("spacing", "sm");

		ArrayNode contents = mapper.createArrayNode();

		ObjectNode button = mapper.createObjectNode();
		button.put("type", "button");
		button.put("flex", 1);
		button.put("margin", "xxl");
		button.put("height", "md");
		button.put("style", "primary");
		button.put("gravity", "center");

		ObjectNode postback = mapper.createObjectNode();
		postback.put("type", "postback");
		postback.put("label", "查看更多");
		postback.put("data", "action=" + action + "&type=" + type + "&page=" + page);

		button.set("action", postback);
		contents.add(button);

		body.set("contents", contents);
		bubble.set("body", body);

		return bubble;
	}
}
