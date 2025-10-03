package com.eventra.linebot.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionLineBotCarouselDTO;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.linebot.util.LineBotFlexBuilder;
import com.eventra.member.model.MemberService;
import com.eventra.order.model.OrderLineBotCarouselDTO;
import com.eventra.order.model.OrderService;
import com.eventra.order.model.OrderStatus;
import com.eventra.order_item.model.OrderItemLineBotCarouselDTO;
import com.eventra.order_item.model.OrderItemService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.util.JsonCodec;

@Service
@Transactional
public class LineBotWebhookService {
	
	// 1. webhook 入口
	// 2. 處理 JSON, 依事件類型分流
		// 有多個 private handleXxxEvent() 方法
		// 有多個 private replyWithXxx() 方法，對應不同訊息格式
	// 3. 最後統一由 sendReply() 發出

	private static final Integer SIZE = 5;
    private final String CHANNEL_SECRET;
    private final String CHANNEL_ACCESS_TOKEN;
    private final JsonCodec JSON_CODEC; 
    private final ExhibitionServiceImpl EXHIBITION_SERVICE;
    private final MemberService MEMBER_SERVICE;
    private final OrderService ORDER_SERVICE;
    private final OrderItemService ORDER_ITEM_SERVICE;

    private final ObjectMapper om = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private final LineBotFlexBuilder FLEX_BUILDER = new LineBotFlexBuilder();

    public LineBotWebhookService(
            @Value("${line.channel.secret}") String channelSecret,
            @Value("${line.channel.access-token}") String channelAccessToken,
            ExhibitionServiceImpl exhibitionService,
            MemberService memberService,
            OrderService orderService,
            OrderItemService orderItemService,
            JsonCodec jsonCodec) {
        this.CHANNEL_SECRET = channelSecret;
        this.CHANNEL_ACCESS_TOKEN = channelAccessToken;
        this.JSON_CODEC = jsonCodec;
        this.EXHIBITION_SERVICE = exhibitionService;
        this.MEMBER_SERVICE = memberService;
        this.ORDER_SERVICE = orderService;
        this.ORDER_ITEM_SERVICE = orderItemService;
    }

    /* ========== 1st part: webhook 入口，主方法 ========== */
    public void handleWebhook(String signature, String body) throws Exception {
    	// 1-1. 驗簽
        if (!verifySignature(body, signature)) {
            throw new SecurityException("Invalid signature");
        }
        // 1-2. 分流
        
        // readTree(String json) 會把 JSON 解析成樹狀結構（JsonNode）
        // 較為動態，不需要先定義成 Java class（DTO）
        // 適合 格式複雜、欄位不固定的 JSON
        JsonNode root = om.readTree(body);
        for (JsonNode event : root.withArray("events")) {
        	// .path 拿欄位用的，可以 chaining 拿巢狀結構最內部 field...
        	// .asText 顧名思義
            String type = event.path("type").asText();
            // message: 使用者傳送訊息（文字、圖片、貼圖、影片...）
            	// payload 裡會有 message.type（text/ image/ sticker/ video/ audio）
            // follow: 使用者 加入好友／掃描 QR Code 加你的bot。常用來發「歡迎訊息」
            // postback: 使用者點選 Postback action（例如 Rich Menu／按鈕，帶隱藏 data）
            	// payload 會有 postback.data, 很適合做「互動表單」、「查詢」這類指令。
            
            /* ===== 以上3者最基本，可處理60%以上情境 =====*/
            
            // unfollow: 使用者把 bot 封鎖。不會有 replyToken（因為被封鎖就回不了），通常只用來紀錄。
            // join: Bot 被邀請進 群組 / 多人聊天室。可以發「大家好！」之類。
            // leave: Bot 被踢出群組 / 聊天室。跟 unfollow 一樣，只能紀錄，不能回。
            // memberJoined/ memberLeft: 有新成員加入 / 離開群組。如果你做活動群組，可能會用到。
            
            System.out.println("===== LINE BOT WEBHOOK: " + type + " =====");
            switch (type) {
                case "message" -> handleMessageEvent(event);
                case "follow" -> handleFollowEvent(event);
                case "postback" -> handlePostbackEvent(event);
                default -> System.out.println("Unhandled event: " + type);
            }
        }
        
    }
    
    /* ========== 2nd part: Event Handlers 根據訊息格式的分流 ========== */
    private void handleFollowEvent(JsonNode event) throws Exception {
        String replyToken = event.path("replyToken").asText();
//        replyWithText(replyToken, "感謝加入好友！輸入『查展覽』開始體驗。");
        try { Thread.sleep(100); }
        catch (InterruptedException e) { System.out.println(e.toString()); }
        replyWithQuickReplyBindAccount(replyToken, "💡小提示：建議先完成 Eventra 會員綁定以便使用完整查詢功能（若已綁定會員請忽略）");
    }
    
    private void handleMessageEvent(JsonNode event) throws Exception {
        String replyToken = event.path("replyToken").asText();
        // text, image, sticker -> 只處理 text (其他的要拿 id 去 messaging api 抓實際圖片檔
        String userText = event.path("message").path("text").asText();
        System.out.println(userText);

        // 簡單分支：文字指令
        if ("如何綁定 LINE 帳號？".equals(userText)) {
        	replyWithQuickReplyBindAccount(replyToken, "您好，請點擊以下按鈕綁定 Eventra 會員！");
        } else if ("推薦展覽有哪些？".equals(userText)) {
        	replyWithQuickReplyExhibition(replyToken, "您好，今天想要找「熱門展覽」、「即將開展的展覽」還是「最新展覽」呢？");
        } else if ("離我最近的展覽有哪些？".equals(userText)) {
            replyWithQuickReplyLocation(replyToken, "請分享您的位置，我將為您尋找最近的店家。");
        } else if ("查詢我的訂單！".equals(userText)){
        	// 1. 判斷有沒有綁 line id
        	String lineUserId = event.path("source").path("userId").asText();
        	// 1-1. 有
        	if(MEMBER_SERVICE.checkIfLineUserIdBound(lineUserId) == true) {
        		replyWithQuickReplyMyOrder(replyToken, "您想查詢哪種訂單狀態的訂單明細呢？（💡小提示：查詢已付款訂單之明細可以領取 QR Code 哦！）");
        	// 1-2. 無
        	} else {
        		replyWithQuickReplyBindAccount(replyToken, "您尚未將此 LINE 帳號綁定至 Eventra 會員哦！");
        	}
        
        } else if (Objects.equal("location", event.path("message").path("type").asText())){
        	Double lat = event.path("message").path("latitude").asDouble(); 
        	Double lng = event.path("message").path("longitude").asDouble();
        	// 手動開啟一次 carousel 循環（不像其他是從 postback message 開始）
        	int page = 0;
        	String action = "search_exhibition";
        	String type = "nearest";
            Slice<ExhibitionLineBotCarouselDTO> nearestList = EXHIBITION_SERVICE.findNearestExhibitionsForLineBot(lat, lng, page, SIZE);
            
        	ObjectNode carousel = FLEX_BUILDER.buildExhibitionCarousel(nearestList.getContent());
        	String json = FLEX_BUILDER.wrapFlexReply(replyToken, carousel);
            sendReply(json);
        }
        else {
        	replyWithQuickReplyDefault(replyToken, "歡迎來到 Eventra！請從以下按鈕選擇指示！");
        }
    }

    private void handlePostbackEvent(JsonNode event) throws Exception {
    	String replyToken = event.path("replyToken").asText();

    	// 解析 data
    	String data = event.path("postback").path("data").asText();
    	Map<String, String> params = Arrays.stream(data.split("&"))
    			.map(s -> s.split("="))
    			.filter(arr -> arr.length == 2)
    			.collect(Collectors.toMap(a -> a[0], a -> a[1]));
    	
    	String action = params.get("action");
    	
    	/* ========== 1st part ========== */
    	// "推薦展覽有哪些？" -> quick reply -> 點擊 quick reply -> 此處處理！
    	if ("search_exhibition".equals(action)) {
    		
        	String type = params.get("type");
        	Integer page = Integer.valueOf(params.get("page"));
        	System.out.println("search_exhibition ---> action=" + action + ", type=" + type + ", page=" + page);

        	String json = null;
        	
            if ("hot".equals(type)) {
                // 呼叫熱門展覽 service
//            	List<ExhibitionLineBotCarouselDTO> hotList = EXHIBITION_SERVICE.findHotExhibitionsForLineBot();
//            	ObjectNode carousel = FLEX_BUILDER.buildCarousel(hotList);
//            	String carouselJson = JSON_CODEC.write(carousel);
//            	// 處理 page
//            	sendReply(carouselJson);
            }  else if ("upcoming".equals(type)) {
                // 呼叫最新展覽 service
            	Slice<ExhibitionLineBotCarouselDTO> upcomingList = EXHIBITION_SERVICE.findUpcomingExhibitionsForLineBot(page, SIZE);
            	ObjectNode carousel = FLEX_BUILDER.buildExhibitionCarousel(upcomingList.getContent(), upcomingList.hasNext(), action, type, page + 1);
            	json = FLEX_BUILDER.wrapFlexReply(replyToken, carousel);
            } else if ("new".equals(type)) {
                // 呼叫最新展覽 service
//            	List<ExhibitionLineBotCarouselDTO> newList = EXHIBITION_SERVICE.findNewExhibitionsForLineBot();
//            	ObjectNode carousel = FLEX_BUILDER.buildCarousel(newList);
//            	String carouselJson = JSON_CODEC.write(carousel);
//            	// 處理 page
//            	sendReply(carouselJson);
            } 
//            else if ("nearest".equals(type)) {
//            	Double lat = event.path("message").path("latitude").asDouble(); 
//            	Double lng = event.path("message").path("longitude").asDouble();
//            	// 手動開啟一次 carousel 循環（不像其他是從 postback message 開始）
//                Slice<ExhibitionLineBotCarouselDTO> nearestList = EXHIBITION_SERVICE.findNearestExhibitionsForLineBot(lat, lng, page, SIZE);
//            	ObjectNode carousel = FLEX_BUILDER.buildExhibitionCarousel(nearestList.getContent(), nearestList.hasNext(), action, type, page + 1);
//            	json = FLEX_BUILDER.wrapFlexReply(replyToken, carousel);
//            }
            sendReply(json);
    	}

    	/* ========== 2nd part ========== */
    	else if ("search_order".equals(action)) {
        	String type = params.get("type");
        	Integer page = Integer.valueOf(params.get("page"));
        	System.out.println("search_order --->  action=" + action + ", type=" + type + ", page=" + page);
    		String lineUserId = event.path("source").path("userId").asText();
    		
    		OrderStatus orderStatus = switch (type) {
    			case "1" -> OrderStatus.已付款;
    			case "2" -> OrderStatus.付款中;
    			case "3" -> OrderStatus.付款失敗;
    			case "4" -> OrderStatus.付款逾時;
    			case "5" -> OrderStatus.已退款;
    			default -> OrderStatus.已付款; // 必須有
    		};
    		
    		Slice<OrderLineBotCarouselDTO> orders = ORDER_SERVICE.findOrdersByLineUserId(lineUserId, orderStatus, page, SIZE);
    		// fallback
    		if(orders == null || orders.isEmpty()) replyWithText(replyToken, "您目前沒有「" + orderStatus.toString() + "」狀態的訂單哦！");
    		
    		ObjectNode carousel = FLEX_BUILDER.buildOrderCarousel(orders.getContent(), orders.hasNext(), action, type, page + 1);
        	String json = FLEX_BUILDER.wrapFlexReply(replyToken, carousel);
        	sendReply(json);
    	}
    	
    	/* ========== 3rd part ========== */
    	// action=get_ticket_qr&orderId=" + o.getOrderUlid() + "&page=0");
    	else if ("get_ticket_qr".equals(action)) {
    		String orderUlid = params.get("orderUlid");
        	Integer page = Integer.valueOf(params.get("page"));
        	System.out.println("search_order ---> action=" + action + ", orderUlid=" + orderUlid + ", page=" + page);
        	String lineUserId = event.path("source").path("userId").asText();
        	
        	Slice<OrderItemLineBotCarouselDTO> orderItems = ORDER_ITEM_SERVICE.findOrderItemsByLineUserId(lineUserId, orderUlid, OrderStatus.已付款, page, SIZE);
        	ObjectNode carousel = FLEX_BUILDER.buildOrderItemCarousel(orderItems.getContent(), orderItems.hasNext(), action, orderUlid, page + 1);
        	String json = FLEX_BUILDER.wrapFlexReply(replyToken, carousel);
        	System.out.println(json.toString());
        	sendReply(json);
    	}
    }

    // ========== Replies: 常見 ==========
    // 1~5: Text Message, Quick Reply, Sticker Message, Image/ Video/ Audio, Location
    // 6. Template Message: 可互動的「卡片」訊息：包含：
    	// Buttons template
    	// Confirm template
    	// Carousel template
    	// Image carousel template
    // 7. Flex Message
    
    private void replyWithText(String replyToken, String text) throws Exception {
        String json = """
        {
          "replyToken": "%s",
          "messages": [{ "type": "text", "text": "%s" }]
        }
        """.formatted(replyToken, text);
        sendReply(json);
    }

    private void replyWithQuickReplyExhibition(String replyToken, String text) throws Exception {
    	// postback -> 傳送隱藏 data 給 webhook -> {"type": "postback", "label": "我要付款", "data": "action=pay&itemid=123"}
        String json = """
        {
          "replyToken": "%s",
          "messages": [{
            "type": "text",
            "text": "%s",
            "quickReply": {
              "items": [
                { "type": "action", "action": { "type": "postback", "label": "熱門展覽", "data": "action=search_exhibition&type=hot&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "即將開展的展覽", "data": "action=search_exhibition&type=upcoming&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "最新展覽", "data": "action=search_exhibition&type=new&page=0" } }
              ]
            }
          }]
        }
        """.formatted(replyToken, text);
        sendReply(json);
    }
    
    private void replyWithQuickReplyLocation(String replyToken, String text) throws Exception {
        String json = """
        {
          "replyToken": "%s",
          "messages": [{
            "type": "text",
            "text": "%s",
        	"quickReply": {
    		    "items": [
    		      {
    		        "type": "action",
    		        "action": {
    		          "type": "location",
    		          "label": "分享我的位置"
    		        }
    		      }
    		    ]
    		  }
          }]
        }
        """.formatted(replyToken, text);
        sendReply(json);
    }
    
    private void replyWithQuickReplyMyOrder(String replyToken, String text) throws Exception {
    	// postback -> 傳送隱藏 data 給 webhook -> {"type": "postback", "label": "我要付款", "data": "action=pay&itemid=123"}
        String json = """
        {
          "replyToken": "%s",
          "messages": [{
            "type": "text",
            "text": "%s",
            "quickReply": {
              "items": [
                { "type": "action", "action": { "type": "postback", "label": "已付款", "data": "action=search_order&type=1&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "付款中", "data": "action=search_order&type=2&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "付款失敗", "data": "action=search_order&type=3&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "付款逾時", "data": "action=search_order&type=4&page=0" } },
                { "type": "action", "action": { "type": "postback", "label": "已退款", "data": "action=search_order&type=5&page=0" } }
              ]
            }
          }]
        }
        """.formatted(replyToken, text);
        sendReply(json);
    }
    
    private void replyWithQuickReplyDefault(String replyToken, String text) throws Exception {
        String json = """
        {
          "replyToken": "%s",
          "messages": [{
            "type": "text",
            "text": "%s",
            "quickReply": {
              "items": [
        		{ "type": "action", "action": { "type": "message", "label": "如何綁定 LINE 帳號？", "text": "如何綁定 LINE 帳號？" } },
                { "type": "action", "action": { "type": "message", "label": "推薦展覽有哪些？", "text": "推薦展覽有哪些？" } },
                { "type": "action", "action": { "type": "message", "label": "離我最近的展覽有哪些？", "text": "離我最近的展覽有哪些？" } },
                { "type": "action", "action": { "type": "message", "label": "查詢我的訂單！", "text": "查詢我的訂單！" } }
              ]
            }
          }]
        }
        """.formatted(replyToken, text);
        sendReply(json);
    }

    private void replyWithQuickReplyBindAccount(String replyToken, String text) throws Exception {
    	String json = """
		        {
		          "replyToken": "%s",
		          "messages": [{
		            "type": "text",
		            "text": "%s",
		            "quickReply": {
		              "items": [
		        		{ "type": "action", "action": { "type": "uri", "label": "立即前往綁定", "uri": "https://eventra.ddns.net/front-end/admin" } }
		              ]
		            }
		          }]
		        }
		        """.formatted(replyToken, text);
		        sendReply(json);
    }
    
    /* ========== 3rd part: webhook 統一回應出口 ========== */
    private void sendReply(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/message/reply"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("LINE Webhook reply API 回應狀態: " + response.statusCode());
        System.out.println("LINE Webhook reply API 回應內容: " + response.body());
    }
    
    // ========== 驗簽 ==========
    // 1. 當 LINE 要丟 webhook event 到我們的 server 的時候，會在 HTTP 請求的 header 附加一個：
    	// X-Line-Signature: base64(HMAC-SHA256(body, channelSecret))
    		// body: 這次 webhook 的 request payload （完整 JSON 字串）
    		// channelSecret: 只有我們跟 LINE 知道的 Channel Secret（只有我們解得開）
    		// HMAC-SHA256: 雜湊演算法，用來確保「body 沒被篡改」
    // 2. 我們收到時
    	// 2-1. 先把 body 用同一個 channelSecret 跑一次 HMAC_SHA256
    	// 2-2. 把計算結果做 base 644 編碼。
    	// 2-3. 比對 header 的 X-Line-Signature 值
    		// 2-3-1. 如果一樣，是 LINE 發來的，可開始處理
    		// 2-3-2. 如果不一樣，直接 403
    
    private boolean verifySignature(String body, String signature) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(CHANNEL_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        byte[] expected = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
        byte[] actual = Base64.getDecoder().decode(signature);

        return MessageDigest.isEqual(expected, actual);
    }
}


