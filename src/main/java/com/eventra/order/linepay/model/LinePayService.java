package com.eventra.order.linepay.model;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.eventra.cart_item.model.CartItemRedisRepository;
import com.eventra.cart_item.model.CartItemRedisVO;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;
import com.eventra.linebot.model.LineBotPushService;
import com.eventra.member.model.MemberVO;
import com.eventra.order.model.OrderLineBotCarouselDTO;
import com.eventra.order.model.OrderProvider;
import com.eventra.order.model.OrderRepository;
import com.eventra.order.model.OrderStatus;
import com.eventra.order.model.OrderVO;
import com.eventra.order_item.model.OrderItemRepository;
import com.eventra.order_item.model.OrderItemVO;
import com.eventra.payment_attempt.model.PaymentAttemptRepository;
import com.eventra.payment_attempt.model.PaymentAttemptStatus;
import com.eventra.payment_attempt.model.PaymentAttemptVO;
import com.github.f4b6a3.ulid.UlidCreator;
import com.util.JsonCodec;
import com.util.ProviderOrderId36Generator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class LinePayService {

	@PersistenceContext
	private EntityManager entityManager;
	
	private final Long ORDER_EXPIRATION_MILLIS;
	
	private String CHANNEL_ID;
	private String CHANNEL_SECRET;
	private String BASE_URL;
	private String CONFIRM_URL_PREFIX;
	private String CANCEL_URL_PREFIX;

	private final RestClient REST_CLIENT;
	private final JsonCodec JSON_CODEC;
	
	private final ExhibitionRepository EXHIBITION_REPO;
	private final CartItemRedisRepository CART_ITEM_REDIS_REPO;
	private final OrderRepository ORDER_REPO;
	private final OrderItemRepository ORDER_ITEM_REPO;
	private final PaymentAttemptRepository PAYMENT_ATTEMPT_REPO;
	
	// line bot push 推播使用
	private final LineBotPushService LINE_BOT_PUSH_SERVICE;

	public LinePayService(
			@Value("${linepay.channel-id}") String channelId,
			@Value("${linepay.channel-secret}") String channelSecret,
			@Value("${linepay.base-url}") String baseUrl,
			@Value("${linepay.confirm-url-prefix}") String confirmUrlPrefix,
			@Value("${linepay.cancel-url-prefix}") String cancelUrlPrefix,
			RestClient.Builder restClientBuilder, JsonCodec jsonCodec, ExhibitionRepository exhibitionRepo, CartItemRedisRepository cartItemRedisRepo,
			OrderRepository orderRepo, OrderItemRepository orderItemRepo, PaymentAttemptRepository paymentAttemptRepo, @Value("${order.expiration-millis}") Long orderExpirationMillis,
			LineBotPushService lineBotPushService) {
		
		this.CHANNEL_ID = channelId;
		this.CHANNEL_SECRET = channelSecret;
		this.BASE_URL = baseUrl;
		this.CONFIRM_URL_PREFIX = confirmUrlPrefix;
		this.CANCEL_URL_PREFIX = cancelUrlPrefix;
		this.REST_CLIENT = restClientBuilder.baseUrl(BASE_URL).build();
		this.JSON_CODEC = jsonCodec;
		this.EXHIBITION_REPO = exhibitionRepo;
		this.CART_ITEM_REDIS_REPO = cartItemRedisRepo;
		this.ORDER_REPO = orderRepo;
		this.ORDER_ITEM_REPO = orderItemRepo;
		this.PAYMENT_ATTEMPT_REPO = paymentAttemptRepo;
		this.ORDER_EXPIRATION_MILLIS = orderExpirationMillis;
		this.LINE_BOT_PUSH_SERVICE = lineBotPushService;
	}

	public void cleanExpiredLinePayPaymentAttempt(PaymentAttemptVO paymentAttempt) {
		System.out.println("clenaing Expired LinePay payment attempts");
		// 1. 拿出 order 等後續要更新的物件
		OrderVO order = paymentAttempt.getOrder();
		String lineUserId = order.getMember().getLineUserId();
		
		// 2. 拿出核心 transactionId
		String transactionId = paymentAttempt.getProviderTransactionId();
		// 3. 呼叫 check api 
		LinePayPaymentRequestCheckResDTO res = paymentRequestCheck(transactionId);
		
		// 4. 從 api res 拆出 returnCode + returnMessage
		String returnCode = res.getReturnCode();
		String returnMessage = res.getReturnMessage();
		paymentAttempt.setRtnCode(returnCode);
		paymentAttempt.setRtnMsg(returnMessage);
		// 5. 開始針對不同 returnCode 做出相對應處理
		
	// 只限於 check api 的狀態碼（需要特別處理的）
		// 0000: 顧客完成 LINE Pay 認證之前的狀態
			// 顧客點了「付款」但還沒去 LINE Pay 畫面輸入資訊，或是正在輸入。例如：使用者停在 LINE Pay 頁面上，但還沒按「確認付款」。
		// 0110: 顧客已完成 LINE Pay 認證，可以進行付款授權
			// 顧客已經在 LINE Pay 輸入資料 & 認證成功了，LINE Pay 這邊確認「可以扣款」，但還需要你系統呼叫 /confirm 來「實際請款」。
		// 0121: 顧客取消付款或超過 LINE Pay 認證等待時間
			// 使用者自己在 LINE Pay 頁面按了「取消付款」，或者他卡在頁面沒動作，超時了。
		// 0122: 付款失敗
			// 意思：顧客有嘗試付款，但銀行端或 LINE Pay 驗證沒過。
			// 常見原因：卡號無效、餘額不足、發卡銀行拒絕交易。
		if("0121".equals(returnCode)) {
			// 1. 過期，掃 expired
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.EXPIRED);
			order.setOrderStatus(OrderStatus.付款失敗);
			// 2. 還可以嘗試付款，並且不釋出票，除非過期就要直接釋出且無法繼續嘗試
			Long createdAt = order.getCreatedAt().getTime();
			if(System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
				// 2-1. order -> 逾時
				order.setOrderStatus(OrderStatus.付款逾時);
				// 2-2. 釋票
				releaseTickets(order);
			}
			if(lineUserId != null) lineBotPush(lineUserId, order);
		}
		if("0122".equals(returnCode)) {
			// 1. 失敗，掃 failure
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.FAILURE);
			order.setOrderStatus(OrderStatus.付款失敗);
			// 2. 還可以嘗試付款，並且不釋出票，除非過期就要直接釋出且無法繼續嘗試
			Long createdAt = order.getCreatedAt().getTime();
			if(System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
				// 2-1. order -> 逾時
				order.setOrderStatus(OrderStatus.付款逾時);
				// 2-2. 釋票
				releaseTickets(order);
			}
			if(lineUserId != null) lineBotPush(lineUserId, order);
		}
		if("0000".equals(returnCode)) {
			// 先當過期，掃 expired
				// 但不過若後續成功，要檢查
					// 是否重複付款
					// 還有票 -> 重新保留，調整本來訂單狀態，並且打 confirm api
					// 沒票 -> 取消授權, paymentAuthorizationsVoid api
			
			// 1. 過期，掃 expired
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.EXPIRED);
			order.setOrderStatus(OrderStatus.付款失敗);
			// 2. 還可以嘗試付款，並且不釋出票，除非過期就要直接釋出且無法繼續嘗試
			Long createdAt = order.getCreatedAt().getTime();
			if(System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
				// 2-1. order -> 逾時
				order.setOrderStatus(OrderStatus.付款逾時);
				// 2-2. 釋票
				releaseTickets(order);
			}
			if(lineUserId != null) lineBotPush(lineUserId, order);
		}
		if("0110".equals(returnCode)) {
			// 成功，可呼叫confirm api
			// 是要送 providerOrderId 而不是 providerTransactionId
			String providerOrderId = paymentAttempt.getProviderOrderId();
			paymentConfirm(providerOrderId);
		}
	}
	
	public LinePaySendingResDTO resendPaymentRequest(String orderUlid) {
		LinePaySendingResDTO res = new LinePaySendingResDTO();
		/* ================================================= */
		OrderVO order = ORDER_REPO.findByOrderUlid(orderUlid);
		Integer orderId = order != null ? order.getOrderId() : null;
		if (orderId == null || !OrderStatus.付款失敗.equals(order.getOrderStatus()))
			// 1. 送錯或亂送 ulid 會造成此狀況！不要亂送！
			// 2. 狀態不是指定狀態！不要亂送！
			return res.setStatus(LinePaySendingStatus.FAILURE_NOT_FOUND)
						.setMessage(LinePaySendingStatus.FAILURE_NOT_FOUND.getMessage());
		
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findTopByOrderIdOrderByCreatedAtDesc(orderId).orElseThrow();
		// 狀態更動
		order.setOrderStatus(OrderStatus.付款中);
		// 先拿回舊的 prReq
		LinePayPaymentRequestReqDTO prReq = JSON_CODEC.read(paymentAttempt.getPackagesJson(), LinePayPaymentRequestReqDTO.class);
		// 調整成新的 prReq
		String providerOrderId = ProviderOrderId36Generator.generateOrderId(); // 不是我們自己 db 的 orderId
		prReq
			.setOrderId(providerOrderId)
			.setRedirectUrls(Map.of("confirmUrl", CONFIRM_URL_PREFIX + providerOrderId,
									"cancelUrl", CANCEL_URL_PREFIX + providerOrderId));
		String apiPath = "/v3/payments/request"; // endpoint
		String nonce = UUID.randomUUID().toString(); // 避免重放攻擊，類似一次性密碼，使用過的 nonce 會被標示無效，攻擊者就無法多次請求隨意多次扣款等等
		String message = CHANNEL_SECRET + apiPath + JSON_CODEC.write(prReq) + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);
		LinePayPaymentRequestResDTO prRes =
				REST_CLIENT.post()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(prReq) // 給 Spring 轉 Json 即可
                .retrieve()
                .body(LinePayPaymentRequestResDTO.class);
		/* ********* 4th part : 確認 payment request 狀況  ********* */
		// 4-1 失敗就回傳
		if(!"0000".equals(prRes.getReturnCode())) {// API呼叫成功時，傳回0000值。其他結果碼均為錯誤碼;
			System.out.println(prRes.getReturnMessage());
			
			return res
					.setStatus(LinePaySendingStatus.FAILURE_LINE_PAY)
					.setMessage(LinePaySendingStatus.FAILURE_LINE_PAY.getMessage());
		}
		
		// 4-2 成功才繼續！
		
		// 5. [db] payment_attempt 建立
		PaymentAttemptVO paymentAttemptNew = new PaymentAttemptVO.Builder()
						.paymentAttemptStatus(PaymentAttemptStatus.PENDING)
						.order(order)
						.provider(OrderProvider.LINEPay)
						.providerOrderId(providerOrderId)
						.tradeAmt(paymentAttempt.getTradeAmt())
						.currency("TWD")
						.packagesJson(JSON_CODEC.write(prReq)) // 寫入新版
						.providerTransactionId(prRes.getInfo().getTransactionId())
						.buildLinePay();
		
        PAYMENT_ATTEMPT_REPO.save(paymentAttemptNew);
        
		return res
				.setStatus(LinePaySendingStatus.SUCCESS)
				.setMessage(prRes.getInfo().getPaymentUrl().getWeb());
	}
	
	// 1. payment request 送出 LINE PAY 訂單，拿到 redirectUrl.web 丟給前端 redirect
	// POST /v3/payments/request
	public LinePaySendingResDTO paymentRequest(LinePaySendingReqDTO req, Integer memberId) {
		
		LinePaySendingResDTO res = new LinePaySendingResDTO();
		/* ********* 1st part : 從 ids 拉掉指定購物車明細 ********* */
		List<Integer> cartItemIds = req.getCartItemIds();
		List<CartItemRedisVO> vos = CART_ITEM_REDIS_REPO.removeCartItem(cartItemIds, memberId);
		if (vos == null || vos.isEmpty())
			return res
					.setStatus(LinePaySendingStatus.FAILURE_CART_ITEM)
					.setMessage(LinePaySendingStatus.FAILURE_CART_ITEM.getMessage());

		String orderUlid = UlidCreator.getUlid().toString();
		Integer totalAmount= vos.stream().collect(Collectors.summingInt(vo -> vo.getQuantity() * vo.getPrice()));
		Integer totalQuantity= vos.stream().collect(Collectors.summingInt(CartItemRedisVO::getQuantity));
		
		/* ********* 2nd part : LinePayPaymentRequestReqDTO 內容準備  ********* */
		// 2-1. List<LinePayPaymentRequestReqDTO.Package.Product>
		List<LinePayPaymentRequestReqDTO.Package.Product> products = new ArrayList<>();
		
		for(CartItemRedisVO vo : vos) {
			LinePayPaymentRequestReqDTO.Package.Product product =
					new LinePayPaymentRequestReqDTO.Package.Product();
			product
			.setName(vo.getExhibitionName() + "   " + vo.getTicketTypeName())
			.setQuantity(vo.getQuantity())
			.setPrice(vo.getPrice());
			
			products.add(product);
		}
		
		// 2-2. LinePayPaymentRequestReqDTO.Package
		LinePayPaymentRequestReqDTO.Package pkg = new
				LinePayPaymentRequestReqDTO.Package();
		pkg.setId(orderUlid) // 一個 pkg 相當於 訂單，用 orderUlid 來記錄
			.setAmount(totalAmount)
			.setProducts(products);
		
		// 2-3. List.of(Package)
		LinePayPaymentRequestReqDTO prReq = new LinePayPaymentRequestReqDTO();
		String providerOrderId = ProviderOrderId36Generator.generateOrderId(); // 不是我們自己 db 的 orderId
		
		prReq.setAmount(totalAmount)
			.setCurrency("TWD")
			.setOrderId(providerOrderId)
			.setPackages(List.of(pkg))
			.setRedirectUrls(Map.of("confirmUrl", CONFIRM_URL_PREFIX + providerOrderId,
									"cancelUrl", CANCEL_URL_PREFIX + providerOrderId));
		
		
		/* ********* 3rd part : 打 LINEPAY endpoint - payment request  ********* */
		// Content-Type	                    String	 必填   設定application/json。
		// X-LINE-Authorization	            String	 必填   HMAC SHA256 演算法生成的credentials
		// X-LINE-Authorization-Nonce	    String	 必填   隨機生成的UUID。v1或v4，或請求時提供時間戳
		// X-LINE-ChannelId	                String	 必填   通訊管道ID	
		// X-LINE-MerchantDeviceProfileId	String	 選填   設備配置文檔 ID。以後，可以在LINE Pay提供的報告書中確認不同裝置的統計。主要輸入合作商店終端機的序號，必要時可使用。請與X-LINE-MerchantDeviceType標頭一同輸入。
		// X-LINE-MerchantDeviceType	    String	 選填   設備類型。請指定合作商店用於區分指定的設備類型。請與X-LINE-MerchantDeviceProfileId標頭一同指定。
		
		String apiPath = "/v3/payments/request"; // endpoint
		String nonce = UUID.randomUUID().toString(); // 避免重放攻擊，類似一次性密碼，使用過的 nonce 會被標示無效，攻擊者就無法多次請求隨意多次扣款等等
		String message = CHANNEL_SECRET + apiPath + JSON_CODEC.write(prReq) + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);

		LinePayPaymentRequestResDTO prRes =
				REST_CLIENT.post()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(prReq) // 給 Spring 轉 Json 即可
                .retrieve()
                .body(LinePayPaymentRequestResDTO.class);
		
//		String returnCode
//		String returnMessage
//		Info info
//			String transactionId
//			PaymentUrl paymentUrl
//				String web
//				String app
	    
		/* ********* 4th part : 確認 payment request 狀況  ********* */
		// 4-1 失敗就回傳，不建訂單
		if(!"0000".equals(prRes.getReturnCode())) {// API呼叫成功時，傳回0000值。其他結果碼均為錯誤碼;
			System.out.println(prRes.getReturnMessage());
			
			return res
					.setStatus(LinePaySendingStatus.FAILURE_LINE_PAY)
					.setMessage(LinePaySendingStatus.FAILURE_LINE_PAY.getMessage());
		}
		
		// 4-2 成功才繼續！
		
		/* ********* 5th part : 建立 order, order_item, payment_attempt  ********* */
		
		// 5-1. [db] order 建立
		MemberVO member = entityManager.getReference(MemberVO.class, memberId);

		OrderVO order = new OrderVO.Builder()
				.orderUlid(orderUlid)
				.orderProvider(OrderProvider.LINEPay)
				.orderStatus(OrderStatus.付款中)
				.member(member)
				.totalAmount(totalAmount)
				.totalQuantity(totalQuantity)
				.build();
		
		ORDER_REPO.save(order);
		
		// 5-2. [db] order_item 建立
		List<OrderItemVO> orderItems = vos.stream()
				.flatMap(vo -> IntStream.range(0, vo.getQuantity())
				.mapToObj(i -> {
					OrderItemVO orderItem = new OrderItemVO.Builder()
							.order(order)
							.orderItemUlid(orderUlid + "-" + vo.getCartItemId() + (i + 1))
							.exhibitionTicketType(entityManager.getReference(ExhibitionTicketTypeVO.class, vo.getExhibitionTicketTypeId()))
							.unitPrice(vo.getPrice())
							.build();
					return orderItem;
				})).collect(Collectors.toList());
		
		ORDER_ITEM_REPO.saveAll(orderItems);
		
		// 5-3. [db] payment_attempt 建立
		PaymentAttemptVO paymentAttempt = new PaymentAttemptVO.Builder()
						.paymentAttemptStatus(PaymentAttemptStatus.PENDING)
						.order(order)
						.provider(OrderProvider.LINEPay)
						.providerOrderId(providerOrderId)
						.tradeAmt(totalAmount)
						.currency("TWD")
						.packagesJson(JSON_CODEC.write(prReq)) // 要存這個嗎？
						.providerTransactionId(prRes.getInfo().getTransactionId())
						.buildLinePay();
		
        PAYMENT_ATTEMPT_REPO.save(paymentAttempt);
		
        /* ********* 6th part : 回傳前端所需 web paymentUrl  ********* */
		return res
				.setStatus(LinePaySendingStatus.SUCCESS)
				.setMessage(prRes.getInfo().getPaymentUrl().getWeb());
	}
	
	// 2. check payment request 確認用戶授權狀況（用戶導入 line pay 可能直接跳開、關瀏覽器等）
	// GET /v3/payments/requests/{transactionId}/check
	// **目前** 只有透過 clean expired line pay payment attempt 來呼叫
	public LinePayPaymentRequestCheckResDTO paymentRequestCheck(String transactionId) {
		
		System.out.println("clenaing Expired LinePay payment attempts -- inside function");
		
		String apiPath = "/v3/payments/requests/" + transactionId + "/check"; // endpoint
		String nonce = UUID.randomUUID().toString(); // 避免重放攻擊，類似一次性密碼，使用過的 nonce 會被標示無效，攻擊者就無法多次請求隨意多次扣款等等
		String message = CHANNEL_SECRET + apiPath + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);

		LinePayPaymentRequestCheckResDTO prcRes =
				REST_CLIENT.get()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .retrieve()
                .body(LinePayPaymentRequestCheckResDTO.class);
		
		return prcRes;
	}
	
	@Async
	// 預設會用 SimpleAsyncTaskExecutor，它不是標準的執行緒池，而是「每次呼叫就開新 thread」。
	// 這在生產環境容易造成 thread 爆炸，所以通常會自訂一個 ThreadPoolExecutor。-> 之後再說
	public void retryPaymentConfirm(String providerOrderId) {
		
		try { Thread.sleep(3000); }
		catch(InterruptedException e) { Thread.currentThread().interrupt(); } // 保持中斷 
		
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElse(null);
		
		if (paymentAttempt == null) return;
		
		Byte retryCount = paymentAttempt.getConfirmApiRetryCount();
		if(retryCount <= 5) { // 1, 2, 3, 4, 5 共 5 次
			paymentAttempt.setConfirmApiRetryCount((byte)(retryCount + 1));
			PAYMENT_ATTEMPT_REPO.save(paymentAttempt);
			paymentConfirm(providerOrderId);
		}
	}
	
	// 3-1. confirm request 用戶授權完成回到 confirmUrl 以後 
	// POST /v3/payments/{transactionId}/confirm
	public String paymentConfirm(String providerOrderId) {
		
		
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElse(null);
		OrderVO order = paymentAttempt.getOrder();
		String lineUserId = order.getMember().getLineUserId();
		
		if(paymentAttempt == null) return null; // -> 付款成功 line pay 導用戶回來的頁面 有人亂輸入的話 要處理
		
		PaymentAttemptStatus status = paymentAttempt.getPaymentAttemptStatus();
		// 重複付款處理
		// -> 這條 payment 還沒成功付款，但其他條 payment 早就成功付款過了！！
		if(status != PaymentAttemptStatus.SUCCESS &&
			order.getOrderStatus() == OrderStatus.已付款) {
			// 呼叫取消此次授權（避免用戶重複保留的款項 多被扣住一週（？））
			// 其實不呼叫也可，但就是用戶的那筆錢會被多扣著一週（？）
			paymentAuthorizationsVoid(providerOrderId);
			return null;
		}
		
		// 重複通知處理 
		// -> SUCCESS -> 這條 payment 已經更新成功過了 -> Line pay 重送的通知
		// -> CHECKING -> 不理，照常跑
		if(status == PaymentAttemptStatus.SUCCESS &&
			order.getOrderStatus() == OrderStatus.已付款) {
			return null;
		}

		LinePayPaymentConfirmReqDTO pcReq = new LinePayPaymentConfirmReqDTO();
		pcReq.setAmount(paymentAttempt.getTradeAmt()).setCurrency(paymentAttempt.getCurrency());
		
		String providerTransactionId = paymentAttempt.getProviderTransactionId();
		String apiPath = "/v3/payments/" + providerTransactionId + "/confirm"; // endpoint
		String nonce = UUID.randomUUID().toString(); // 避免重放攻擊，類似一次性密碼，使用過的 nonce 會被標示無效，攻擊者就無法多次請求隨意多次扣款等等
		String message = CHANNEL_SECRET + apiPath + JSON_CODEC.write(pcReq) + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);

		LinePayPaymentConfirmResDTO prRes =
				REST_CLIENT.post()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(pcReq)
                .retrieve()
                .body(LinePayPaymentConfirmResDTO.class);
		
		String returnCode = prRes.getReturnCode();
		String returnMessage = prRes.getReturnMessage();
		paymentAttempt.setRtnCode(returnCode);
		paymentAttempt.setRtnMsg(returnMessage);
		System.out.println(returnCode + ": " + returnMessage);
		
		// 失敗 -> confirm url 回來但 confirm api 失敗... 使用者帳戶已經保留款項，但我們沒有請款成功 !!
		if(!"0000".equals(returnCode)) {
			System.out.println("failed~~~~~~~~~~~~~~~~~~~~~~~~");
			// 0. payment attempt 先設定為 checking 確保不會 order 被掃成逾時
			Byte retryCount = paymentAttempt.getConfirmApiRetryCount();
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.CHECKING);
			paymentAttempt.setConfirmApiRetryCount((byte)(retryCount + 1));
			PAYMENT_ATTEMPT_REPO.save(paymentAttempt);
			// 1. 立即重打 api -> confirm API 是 冪等的，重複呼叫同一個 transactionId 不會重複扣款。
			if(paymentAttempt.getConfirmApiRetryCount() <= 1)
				retryPaymentConfirm(paymentAttempt.getProviderOrderId());
			// 2. 後續由上方的 @Async job 掃描... 
			return null;
		}
		
		// 成功
		if("0000".equals(returnCode)) {
		/* ********* 1st step: paymentAttempt, order, orderItems 更新狀態 ********* */
			// 1-1: paymentAttempt
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.SUCCESS);
			// 1-2: order
			order.setOrderStatus(OrderStatus.已付款);
			// 1-3: orderItems -> 產票號
			Set<OrderItemVO> orderItems = order.getOrderItems();
			for(OrderItemVO orderItem : orderItems)
				if(orderItem.getTicketCode() == null) // 這裡會有必要嗎？
					orderItem.setTicketCode(genTicketCode(orderItem.getOrderItemUlid()));
			
		}
		/* ********* *th step : line bot push message ********* */
		if(lineUserId != null) lineBotPush(lineUserId, order); // 這 method 中狀況基本只有 confirm 也成功才出去
				
		return null;
	}
	
	// 3-2. 用戶授權失敗 -> 回到 cancelUrl
	// no api related (only db update)
	public void paymentCancel(String providerOrderId) {
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElseThrow();
		OrderVO order = paymentAttempt.getOrder();
		String lineUserId = order.getMember().getLineUserId();
		
		// 1. 失敗，掃 failure
		paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.FAILURE);
		order.setOrderStatus(OrderStatus.付款失敗);
		
		// 2. 還可以嘗試付款，並且不釋出票，除非過期就要直接釋出且無法繼續嘗試
		Long createdAt = order.getCreatedAt().getTime();
		if(System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
			// 2-1. order -> 逾時
			order.setOrderStatus(OrderStatus.付款逾時);
			// 2-2. 釋票
			releaseTickets(order);
		}
		if(lineUserId != null) lineBotPush(lineUserId, order);
	}
	
	// 4. 取消授權 -> payment attempt 過期回來沒票就要主動取消授權
	// POST /v3/payments/authorizations/{transactionId}/void
	// **目前** 只會被 confirm url 回來重複付款的方法呼叫
	public void paymentAuthorizationsVoid(String providerOrderId) {
		// 使用者體驗
			// 1. 等過期：要等 LINE Pay 的授權到期（通常 7 天），這段期間顧客信用卡額度會被占用。
				// 假設顧客額度 10,000，授權 5,000，雖然錢沒扣走，但額度只剩 5,000 可用。
				// 顧客這時去刷別的東西可能會失敗，體驗不好。
			// 2. 立即取消授權：商戶打 void API，授權立刻釋放，顧客額度馬上恢復。
				// 👉 這就是為什麼支付平台（LINE Pay、ECPay、Stripe）都會建「取消授權 API」：是為了顧客體驗。
		
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElseThrow();
		
		String providerTransactionId = paymentAttempt.getProviderTransactionId();
		
		System.out.println(providerTransactionId);
		
		
//		String apiPath = "/v3/payments/" + providerTransactionId + "/refund"; // endpoint
		String apiPath = "/v3/payments/authorizations/" + providerTransactionId + "/void"; // endpoint
		String nonce = UUID.randomUUID().toString(); // 避免重放攻擊，類似一次性密碼，使用過的 nonce 會被標示無效，攻擊者就無法多次請求隨意多次扣款等等
		String message = CHANNEL_SECRET + apiPath + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);

		System.out.println(apiPath);
		
		 LinePayPaymentVoidResDTO pvRes =
				REST_CLIENT.post()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .retrieve()
                .body(LinePayPaymentVoidResDTO.class);
		 
		 System.out.println(pvRes.getReturnCode() + ": " + pvRes.getReturnMessage());
		 
		 // 測試目前取消授權都會失敗 1150
		 // 可能原因1
		 	// 如果你在 check 看到 reserved transaction，但其實它的 paymentType 不是 credit-card reservation（例如有些情況是 immediate capture），那 LINE Pay 會回 1150。
		 // 可能原因2
		 	// 有些交易 flow 在 sandbox 裡會 永遠回 1150，因為 sandbox 沒有真的維護授權池。
		 	// LINE 官方 FAQ 提到過：sandbox 的授權交易有時候不能成功 void，因為它們會直接 auto-confirm。
		 
		 // 取消授權失敗
		 if(!"0000".equals(pvRes.getReturnCode())) {
			 System.out.println("取消授權失敗！");
			 paymentAttempt.setIsDuplicateResolved(false);
		 }
		 // 取消授權成功
		 else if ("0000".equals(pvRes.getReturnCode())) {
			 System.out.println("取消授權成功！");
			 paymentAttempt.setIsDuplicateResolved(true);
		 }
	}
	
	// 5. 查詢已授權的付款 -> 不能拿這條來查未完成授權的認證階段資訊
	// GET /v3/payments
	public void paymentCheck() {
	}
	
	// 6. capture(一般來說會跟confirm綁一起）後想退款（非單純是用戶授權後capture尚未完成 -> 那是另一條 api）
	// POST /v3/payments/{transactionId}/refund
	public void paymentRefund() {
		
	}
	
	private void lineBotPush(String lineUserId, OrderVO order) { 
		OrderLineBotCarouselDTO dto = new OrderLineBotCarouselDTO();
		dto.setOrderStatus(order.getOrderStatus())
			.setOrderUlid(order.getOrderUlid())
			.setTotalAmount(order.getTotalAmount())
			.setTotalQuantity(order.getTotalQuantity());
		
		// 如果有 lineUserId 才推播
		try { LINE_BOT_PUSH_SERVICE.pushOrder(lineUserId, dto); }
		catch (Exception e) {System.out.println(e.toString());}
	}
	
	private void releaseTickets(OrderVO order) {
		Map<Integer, Integer> releaseMap = order.getOrderItems().stream().collect(Collectors.groupingBy(
				item -> item.getExhibitionTicketType().getExhibitionId(), Collectors.summingInt(item -> 1)));
			for (Map.Entry<Integer, Integer> entry : releaseMap.entrySet())
				EXHIBITION_REPO.updateSoldTicketQuantity(entry.getKey(), -entry.getValue());
	}
	
	private String genTicketCode(String ulid) {
		return "TK-" + ulid.substring(13, 20) + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-"
				+ ThreadLocalRandom.current().nextInt(1, 9999);
	}
	
	private String signHmacSHA256(String secret, String message) {
		// message -> 將 CHANNEL_SECRET, apiPath, request body, nonce 這些東西組合起來。
		// HMAC-SHA256 演算法的核心，它會用 secret (也就是你的 channel secret) 對 message 進行雜湊運算，得到一個 MAC 值 (Message Authentication Code)。
		try {
			Mac mac = Mac.getInstance("HmacSHA256"); // 初始化（拿到實例）一個用於 HMAC-SHA256 演算法的物件。Mac 類別就是用來處理 MAC 運算的。
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")); // 設定密鑰。它用提供的 secret (也就是 CHANNEL_SECRET) 來初始化 mac 物件。
			byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8)); // 把 message (待簽章的內容) 丟進去，用已經設定好的密鑰進行 HMAC-SHA256 演算法的運算。運算後的結果是一個位元組陣列 (byte[])，這就是原始的 MAC 值。
			return Base64.getEncoder().encodeToString(hmac); // 式轉換。因為原始的 MAC 值是一個位元組陣列，它包含了二進位的資料。為了讓這個值可以在 HTTP Header 這種文字格式的環境中傳輸，需要把它轉換成可讀的字串。Base64 編碼就是一種常見的轉換方式。
		} catch (Exception e) {
			throw new RuntimeException("HMAC generation failed", e);
		}
	}
}
