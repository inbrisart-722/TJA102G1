package com.eventra.linebot.util;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eventra.exhibition.model.ExhibitionLineBotCarouselDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LineBotFlexBuilder {

	private final ObjectMapper mapper = new ObjectMapper();
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm");

	public ObjectNode buildCarousel(List<ExhibitionLineBotCarouselDTO> list, boolean hasNextPage, String action, String type, int nextPage) {
		ObjectNode carousel = mapper.createObjectNode();
		carousel.put("type", "carousel");
		ArrayNode contents = mapper.createArrayNode();

		for (ExhibitionLineBotCarouselDTO ex : list) {
			contents.add(buildBubble(ex));
		}
		
		 // 如果還有下一頁，就加查看更多
	    if (hasNextPage) {
	        contents.add(buildSeeMoreBubble(action, type, nextPage));
	    }

		carousel.set("contents", contents);
		return carousel;
	}
	
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


	private ObjectNode buildBubble(ExhibitionLineBotCarouselDTO ex) {
		ObjectNode bubble = mapper.createObjectNode();
		bubble.put("type", "bubble");

		// hero 圖片
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

		// body 區塊
		ObjectNode body = mapper.createObjectNode();
		body.put("type", "box");
		body.put("layout", "vertical");
		ArrayNode bodyContents = mapper.createArrayNode();

		// 標題
		ObjectNode title = mapper.createObjectNode();
		title.put("type", "text");
		title.put("text", ex.getExhibitionName());
		title.put("weight", "bold");
		title.put("size", "xl");
		title.put("margin", "sm");
		bodyContents.add(title);

		// 評分
		bodyContents.add(buildRatingBox(ex.getAverageRatingScore()));

		// 地址
		bodyContents.add(buildInfoRow("📍", ex.getLocation(), null));

		// 開始時間
		bodyContents.add(buildInfoRow("⏲", ex.getStartTime().format(dtf) + " ~", null));

		// 結束時間
		bodyContents.add(buildInfoRow("", ex.getEndTime().format(dtf), "24px"));

		body.set("contents", bodyContents);
		bubble.set("body", body);

		// footer 區塊
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
		for (int i = 0; i < fullStars; i++) {
			ratingContents.add(makeStar("gold"));
		}
		for (int i = fullStars; i < 5; i++) {
			ratingContents.add(makeStar("gray"));
		}

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

}
