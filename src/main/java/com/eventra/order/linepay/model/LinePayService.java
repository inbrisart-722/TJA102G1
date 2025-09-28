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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.eventra.cart_item.model.CartItemRedisRepository;
import com.eventra.cart_item.model.CartItemRedisVO;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;
import com.eventra.member.model.MemberVO;
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

	public LinePayService(
			@Value("${linepay.channel-id}") String channelId,
			@Value("${linepay.channel-secret}") String channelSecret,
			@Value("${linepay.base-url}") String baseUrl,
			@Value("${linepay.confirm-url-prefix}") String confirmUrlPrefix,
			@Value("${linepay.cancel-url-prefix}") String cancelUrlPrefix,
			RestClient.Builder restClientBuilder, JsonCodec jsonCodec, ExhibitionRepository exhibitionRepo, CartItemRedisRepository cartItemRedisRepo,
			OrderRepository orderRepo, OrderItemRepository orderItemRepo, PaymentAttemptRepository paymentAttemptRepo) {
		
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
		if(!prRes.getReturnCode().equals("0000")) {// API呼叫成功時，傳回0000值。其他結果碼均為錯誤碼;
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
	public void paymentRequestCheck() {
		
		
	}
	
	// 3-1. confirm request 用戶授權完成回到 confirmUrl 以後 
	// POST /v3/payments/{transactionId}/confirm
	public String paymentConfirm(String providerOrderId) {
		PaymentAttemptVO paymentAttempt = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElse(null);
		
		if(paymentAttempt == null) return null; // -> 付款成功 line pay 導用戶回來的頁面 有人亂輸入的話 要處理

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
		
		// 失敗
		if(!prRes.getReturnCode().equals("0000")) {
			// do something 
			System.out.println(prRes.getReturnMessage());
		}
		
		// 成功
		if(prRes.getReturnCode().equals("0000")) {
		/* ********* 1st step: paymentAttempt, order, orderItems 更新狀態 ********* */
			// 1-1: paymentAttempt
			paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.SUCCESS);
			// 1-2: order
			OrderVO order = paymentAttempt.getOrder();
			order.setOrderStatus(OrderStatus.已付款);
			// 1-3: orderItems -> 產票號
			Set<OrderItemVO> orderItems = order.getOrderItems();
			for(OrderItemVO orderItem : orderItems)
				if(orderItem.getTicketCode() == null) // 這裡會有必要嗎？
					orderItem.setTicketCode(genTicketCode(orderItem.getOrderItemUlid()));
		}
				
		return null;
	}
	
	// 3-2. 用戶授權失敗 -> 回到 cancelUrl
	// no api related (only db update)
	public void paymentCancel() {
		
	}
	
	
	// 4. 查詢已授權的付款 -> 不能拿這條來查未完成授權的認證階段資訊
	// GET /v3/payments
	public void paymentCheck() {
		
	}
	
	// 5. capture(一般來說會跟confirm綁一起）後想退款（非單純是用戶授權後capture尚未完成 -> 那是另一條 api）
	// POST /v3/payments/{transactionId}/refund
	public void paymentRefund() {
		
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
