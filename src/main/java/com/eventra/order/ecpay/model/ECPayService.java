package com.eventra.order.ecpay.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import com.properties.ECPayProperties;
import com.util.ECPayUtils;
import com.util.ProviderOrderId36Generator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class ECPayService {

	@PersistenceContext
	private EntityManager entityManager;
	
	private final ECPayProperties ECPAY_PROPS;
	private final Long ORDER_EXPIRATION_MILLIS;

	private final ECPayUtils ECPAY_UTILS;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final CartItemRedisRepository CART_ITEM_REDIS_REPO;
	private final OrderRepository ORDER_REPO;
	private final OrderItemRepository ORDER_ITEM_REPO;
	private final PaymentAttemptRepository PAYMENT_ATTEMPT_REPO;
	private final RestClient REST_CLIENT;

	public ECPayService(ECPayProperties ECPayProps, ECPayUtils ECPayUtils, ExhibitionRepository exhibitionRepository,
			CartItemRedisRepository cartItemRedisRepository, OrderRepository orderRepository,
			OrderItemRepository orderItemRepository, PaymentAttemptRepository paymentAttemptRepository,
			RestClient.Builder restClientBuilder, @Value("${order.expiration-millis}") Long orderExpirationMillis) {
		this.ECPAY_PROPS = ECPayProps;
		this.ECPAY_UTILS = ECPayUtils;
		this.EXHIBITION_REPO = exhibitionRepository;
		this.CART_ITEM_REDIS_REPO = cartItemRedisRepository;
		this.ORDER_REPO = orderRepository;
		this.ORDER_ITEM_REPO = orderItemRepository;
		this.PAYMENT_ATTEMPT_REPO = paymentAttemptRepository;
		this.REST_CLIENT = restClientBuilder.baseUrl(ECPAY_PROPS.queryUrl()).build();
		this.ORDER_EXPIRATION_MILLIS = orderExpirationMillis;
	}


	public void cleanExpiredECPayPaymentAttempt(PaymentAttemptVO vo) {
		// 如果是 ECPay -> 打綠界 QueryTradeInfo api 確認沒有更新的 payment attempt 狀態
			Map<String, String> res = ECPayQuery(vo);
			System.out.println("ECPayQuery receiving");

			/* ********* 3rd part : 驗 checkMacValue ********* */
			String checkMac = null;
			try {
				checkMac = ECPAY_UTILS.genCheckMacValue(res);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			// throw new Exception("WRONG checkMacValue");
			if (!res.get("CheckMacValue").equals(checkMac))
				return;
			System.out.println("Query API checkMacValue validated");

			/* ********* 4rd part : 驗其他內容 ********* */
			if (!Objects.equals(vo.getMerchantId(), res.get("MerchantID"))
					|| !Objects.equals(vo.getProviderOrderId(), res.get("MerchantTradeNo"))
					|| !Objects.equals(vo.getTradeAmt(), Integer.valueOf(res.get("TradeAmt")))
					|| !Objects.equals(vo.getItemName(), res.get("ItemName")))
				return;

			/* ********* 5th part : 開始更新 payment attempt ********* */
			vo.setStoreId(res.get("StoreID"));
			vo.setProviderTransactionId(res.get("TradeNo"));
			vo.setRtnCode(res.get("TradeStatus")); // 目前認知是相同意義
//		vo.setRtnMsg(); // 主動 Query 沒有
//		vo.setSimulatePaid(); // 主動 Query 沒有
			vo.setPaymentType(res.get("PaymentType"));
			vo.setPaymentTypeChargeFee(Integer.valueOf(res.get("PaymentTypeChargeFee")));
			vo.setPaymentDate(res.get("PaymentDate"));
			vo.setCheckMacValue(res.get("CheckMacValue"));

			/* ********* 6th part : 基於核心 status 開始更新 payment attempt & order 狀態 ********* */
			OrderVO orderVO = vo.getOrder();
			switch (res.get("TradeStatus")) {
			case "1":
				vo.setPaymentAttemptStatus(PaymentAttemptStatus.SUCCESS);
				orderVO.setOrderStatus(OrderStatus.已付款);
				/* ********* 支線 : 更新 OrderItem 寫入 ticketCode ********* */
				Set<OrderItemVO> orderItemVOs = orderVO.getOrderItems();
				for (OrderItemVO item : orderItemVOs)
					item.setTicketCode(genTicketCode(item.getOrderItemUlid()));
//			ORDER_ITEM_REPO.saveAll(orderItemVOs);
				break;
			case "0":
				vo.setPaymentAttemptStatus(PaymentAttemptStatus.EXPIRED);
				orderVO.setOrderStatus(OrderStatus.付款失敗);
				break;
			default:
				vo.setPaymentAttemptStatus(PaymentAttemptStatus.FAILURE);
				orderVO.setOrderStatus(OrderStatus.付款失敗);
				break;
			}
			Long createdAt = orderVO.getCreatedAt().getTime();
			if (System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
				orderVO.setOrderStatus(OrderStatus.付款逾時);
				// 釋出票出口 1
				releaseTickets(orderVO);
			}
	}
	
	public String ECPayReturnURL(ECPayCallbackReqDTO req) {
		/* ********* 1st part : 從 merchantTradeNo 找到指定 PaymentAttempt ********* */
		String merchantTradeNo = req.getMerchantTradeNo();
		PaymentAttemptVO paVO = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(merchantTradeNo).orElseThrow();
		/* ********* 2rd part : 核對 CheckMacValue 失敗就在此截斷 0|FAIL ********* */
		// 自己拿 map 用我們手上的 HashKey 去算過
		String checkMacValue = null;
		try {
			checkMacValue = ECPAY_UTILS.genCheckMacValue(ECPAY_UTILS.genCheckMap(req));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
//		System.out.println("merchantCheckMacValue: " + req.getCheckMacValue());
//		System.out.println("checkMacValue: " + checkMacValue);

		if (!Objects.equals(req.getCheckMacValue(), checkMacValue))
			return "0|FAILED";
		System.out.println("checkMacValue validated!!! it's ECPay here");
		/* ********* 3rd part : 核對 PaymentAttempt 送出前已存的部分欄位 失敗就在此截斷 0|FAIL ********* */
		System.out.println(req.toString());

		// 避免 nullpointer
		if (!Objects.equals(paVO.getMerchantId(), req.getMerchantID())
				|| !Objects.equals(paVO.getTradeAmt(), req.getTradeAmt()))
			return "0|FAILED";

		/* ********* *rd part : 以防 重複付款 ********* */
		boolean doublePay = false;
		OrderVO orderVO = paVO.getOrder();
		
		// 避免同一筆交易(同一個 TradeNo) 的二次通知，也誤判成重複付款。
		String tradeNo = paVO.getProviderTransactionId();
		
		if (OrderStatus.已付款 == orderVO.getOrderStatus() && tradeNo == null) {
			doublePay = true;
		}
		/* ********* *rd part : 以防 ECPay 多次送出同樣的 ReturnURL ********* */
		else if(tradeNo != null) {
			return "1|OK"; 
		}
		// savedTradeNo 已存在，卻跟新通知不同 → log 下來調查。先略過這段資料異常問題（目前算太過細節） 

		/* ********* 4th part: 先更新共用欄位 ********* */
		paVO.setStoreId(req.getStoreID());
		paVO.setProviderTransactionId(req.getTradeNo());
		paVO.setRtnCode(req.getRtnCode());
		paVO.setRtnMsg(req.getRtnMsg());
		paVO.setSimulatePaid(req.getSimulatePaid());
		paVO.setPaymentType(req.getPaymentType());
		paVO.setPaymentTypeChargeFee(req.getPaymentTypeChargeFee());
		paVO.setPaymentDate(req.getPaymentDate());
		paVO.setCheckMacValue(req.getCheckMacValue());
		/* ********* 5th part : 處理付款成功情況 ********* */
		if ("1".equals(req.getRtnCode())) {
			System.out.println("payment succeeded");
			/* ********* 5-1 part : 更新 PaymentAttempt 填入多項明細 ********* */
			paVO.setPaymentAttemptStatus(PaymentAttemptStatus.SUCCESS);
			/* ********* 5-2 part : 更新 Order 調整 orderStatus ********* */
			if(doublePay == true)
				paVO.setIsDuplicate(true);
			else {
				orderVO.setOrderStatus(OrderStatus.已付款);
			/* ********* 5-3 part : 更新 OrderItem 寫入 ticketCode ********* */
			Set<OrderItemVO> orderItemVOs = orderVO.getOrderItems();
			for (OrderItemVO item : orderItemVOs)
				if (item.getTicketCode() == null)
					item.setTicketCode(genTicketCode(item.getOrderItemUlid()));
			}
		}
		/* ********* 6th part : 處理付款失敗與異常情況 ********* */
		// 10300066：「交易付款結果待確認中，請勿出貨」，請至廠商管理後台確認已付款完成再出貨。
		// 這個特殊 code 留著 放在之後掃過期 payment attempt 時候會再次確認
		else if (!"10300066".equals(req.getRtnCode())) {
			System.out.println("payment failed");
			/* ********* 6-1 part : 更新 PaymentAttempt 填入多項明細 ********* */
			paVO.setPaymentAttemptStatus(PaymentAttemptStatus.FAILURE);
			/* ********* 6-2 part : 更新 Order 調整 orderStatus ********* */
			if(doublePay == false) {
				orderVO.setOrderStatus(OrderStatus.付款失敗);
				/* ********* 6-3 part : 額外確認此訂單是否過期 ********* */
				Long createdAt = orderVO.getCreatedAt().getTime();
				if (System.currentTimeMillis() - createdAt > ORDER_EXPIRATION_MILLIS) {
					orderVO.setOrderStatus(OrderStatus.付款逾時);
					// 釋出票出口 3
					releaseTickets(orderVO);
				}
			}
		}
		/* ********* 7th part : 統一回傳 ECPay 1|OK ********* */
		return "1|OK";
	}

	// 針對已建立之 order 重新送出金流
	public ECPaySendingResDTO ECPayResending(String orderUlid) {
		// 要確認訂單狀態是 "付款失敗" 才讓用戶送 送出當下要立即把訂單狀態調整成 "付款中"
		OrderVO orderVO = ORDER_REPO.findByOrderUlid(orderUlid);
		Integer orderId = orderVO != null ? orderVO.getOrderId() : null;
		if (orderId == null)
			return null; // 送錯或亂送 ulid 會造成此狀況！不要亂送！
		if (!OrderStatus.付款失敗.equals(orderVO.getOrderStatus()))
			return null; // 狀態不是指定狀態！不要亂送！

		orderVO.setOrderStatus(OrderStatus.付款中);

		// no value present ？ 這裡 throw 有機會丟錯嗎 ？
		PaymentAttemptVO paVO = PAYMENT_ATTEMPT_REPO.findTopByOrderIdOrderByCreatedAtDesc(orderId).orElseThrow();
		Integer totalAmount = paVO.getTradeAmt();
		String itemName = paVO.getItemName();
		// 以下 2樣 都要重設重給（尤其是 merchantTradeNo 綠界會擋）
		String merchantTradeNo = ProviderOrderId36Generator.generateMerchantTradeNo();
		String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

		// 以下兩步驟 copy 第一次送出時的拼湊方式
		/* ********* 1th part : 拼湊前端所需資訊 ********* */
		// 除了 fields 要動態拼，其他在 demo 環境下為固定
		ECPaySendingResDTO res = new ECPaySendingResDTO.Builder().status("success").action(ECPAY_PROPS.checkoutUrl())
				.method("POST").fields(fieldsBuilder(merchantTradeNo, merchantTradeDate, totalAmount, itemName))
				.build();
		/* ********* 2th part : 新增 payment attempt 並回傳 ********* */
		// 需要 order, merchantTradeNo, merchantId, tradeAmt, paymentType, checkMacValue
		PaymentAttemptVO paymentAttemptVO = new PaymentAttemptVO.Builder().provider(OrderProvider.ECPay)
				.paymentAttemptStatus(PaymentAttemptStatus.PENDING).order(orderVO).providerOrderId(merchantTradeNo)
				.merchantId(ECPAY_PROPS.merchantId()).tradeAmt(totalAmount).itemName(paVO.getItemName())
//			.paymentType("Credit") // Callback 寫不一樣的...
				.tradeDate(merchantTradeDate).buildECPay();
		
		PAYMENT_ATTEMPT_REPO.save(paymentAttemptVO);

		return res;
	}

	// 首次建立 order 並送出（針對已建立之 order 重新送出請看上面那個方法）
	public ECPaySendingResDTO ECPaySending(ECPaySendingReqDTO req, Integer memberId) {
		/* ********* 1st part : 從 ids 拉掉指定購物車明細 ********* */
		// NoSQL 先動其實有點風險，phase II 可看是否有辦法修正

		List<Integer> cartItemIds = req.getCartItemIds();
		List<CartItemRedisVO> vos = CART_ITEM_REDIS_REPO.removeCartItem(cartItemIds, memberId);
		if (vos == null || vos.isEmpty())
			return null; // 選定的購物車明細已經被清掉！

		/* ********* 先算出所需參數 ********* */
		String itemName = vos.stream()
				.map(vo -> vo.getExhibitionName() + "   " + vo.getTicketTypeName() + ": " + vo.getQuantity() + " 筆")
				.collect(Collectors.joining("#"));
		Integer totalAmount = vos.stream().collect(Collectors.summingInt(vo -> vo.getPrice() * vo.getQuantity()));
		Integer totalQuantity = vos.stream().collect(Collectors.summingInt(vo -> vo.getQuantity()));
		String merchantTradeNo = ProviderOrderId36Generator.generateMerchantTradeNo();
		String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		// 綠界：送出時叫 merchantTradeDate => 回傳時叫 tradeDate
		String ulid = UlidCreator.getUlid().toString();

		/* ********* 2nd part : 新增 order ********* */
		// 需要 orderUlid, orderStatus, member, totalAmount, totalQuantity
		OrderVO orderVO = ORDER_REPO.save(new OrderVO.Builder().orderUlid(ulid).orderProvider(OrderProvider.ECPay).orderStatus(OrderStatus.付款中)
				.member(entityManager.getReference(MemberVO.class, memberId)).totalAmount(totalAmount)
				.totalQuantity(totalQuantity).build());

		/* ********* 3nd part : 新增 order items ********* */
		// 需要 order, orderItemUlid, exhibitionTicketType, unitPrice

		List<OrderItemVO> orderItemVOs = vos.stream().flatMap(vo -> IntStream.range(0, vo.getQuantity())
				// map [1,2] [0,1,2] ; flatMap [1,2,0,1,2]
				.mapToObj(i -> {
					// 把 IntStream / DoubleStream 基元型 stream 轉為 物件流
					OrderItemVO item = new OrderItemVO.Builder().order(orderVO)
							.orderItemUlid(ulid + "-" + vo.getCartItemId() + (i + 1)).exhibitionTicketType(entityManager
									.getReference(ExhibitionTicketTypeVO.class, vo.getExhibitionTicketTypeId()))
							.unitPrice(vo.getPrice()).build();
					return item;
				})).collect(Collectors.toList());

		ORDER_ITEM_REPO.saveAll(orderItemVOs);

		/* ********* 4th part : 拼湊前端所需資訊 ********* */
		// 除了 fields 要動態拼，其他在 demo 環境下為固定
		ECPaySendingResDTO res = new ECPaySendingResDTO.Builder().status("success").action(ECPAY_PROPS.checkoutUrl())
				.method("POST").fields(fieldsBuilder(merchantTradeNo, merchantTradeDate, totalAmount, itemName))
				.build();
		/* ********* 5th part : 新增 payment attempt 並回傳 ********* */
		// 需要 order, merchantTradeNo, merchantId, tradeAmt, paymentType, checkMacValue
		PaymentAttemptVO paymentAttemptVO = new PaymentAttemptVO.Builder().provider(OrderProvider.ECPay)
				.paymentAttemptStatus(PaymentAttemptStatus.PENDING).order(orderVO).providerOrderId(merchantTradeNo)
				.merchantId(ECPAY_PROPS.merchantId()).tradeAmt(totalAmount)
//				.paymentType("Credit") // Callback 寫不一樣的...
				.tradeDate(merchantTradeDate).itemName(itemName).buildECPay();

		PAYMENT_ATTEMPT_REPO.save(paymentAttemptVO);

		return res;
	}

	// No Webflux used here -> and RestClient instead of RestTemplate
	// Spring Boot 3.2+，spring-boot-starter-web 已經內建了 RestClient，不用額外引入。
	public Map<String, String> ECPayQuery(PaymentAttemptVO vo) {
		
		// MultiValueMap(Spring-specific) 才會選 HttpMessageConverter ->
		// FormHttpMessageConverter 並且標頭 application/x-www-form-urlencoded
		// 並且 同 key 不會合併，符合 x-www-form-urlencoded
		// HashMap 的話會變成 MappingJackson2HttpMessageConverter 然後標頭 application/json
		Map<String, String> params = fieldsBuilder(vo.getMerchantId(), vo.getProviderOrderId());
		MultiValueMap<String, String> multiParams = new LinkedMultiValueMap<>();
		multiParams.setAll(params);
		
		// 傳給綠界: MerchantID, MerchantTradeNo, TimeStamp, CheckMacValue, (PlatformID)
		// https://payment-stage.ecpay.com.tw/Cashier/QueryTradeInfo/V5
		// Content Type ：application/x-www-form-urlencoded
		// HTTP Method ：POST
		System.out.println("ECPayQuery sending");
		String res = REST_CLIENT.post().contentType(MediaType.APPLICATION_FORM_URLENCODED).body(multiParams).retrieve()
				.body(String.class);

		Map<String, String> parsedRes = Arrays.stream(res.split("&")).map(kv -> kv.split("=", 2)) // 2 > 最大拆成兩個部分
//				.collect(Collectors.toMap(kv -> kv[0], kv -> kv[1])); // 若確定 = 都一定有寫，用這版即可
				.collect(Collectors.toMap(kv -> kv[0], kv -> kv.length > 1 ? kv[1] : "")); // 否則須處理
																							// ArrayIndexOutOfBoundsException
		// Collectors.toMap(keyMapper, valueMapper, [mergeFunction可選])

		return parsedRes;
		// 綠界回傳: MerchantID, MerchantTradeNo, StoreID, TradeNo, TradeAmt,
		// PaymentDate, PaymentType, HandlingCharge, PaymentTypeChargeFee,
		// TradeDate, TradeStatus, ItemName, CustomFiedl1~4, CheckMacValue
		// 回傳核心是 TradeStatus -> 1 已付款 0 未付款（嘗試中） 10200095 訂單失敗
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

	// used by query api
	private Map<String, String> fieldsBuilder(String merchantId, String merchantTradeNo) {
		Map<String, String> fields = new HashMap<>();
		fields.put("MerchantID", merchantId);
		fields.put("MerchantTradeNo", merchantTradeNo);

		String epoch = String.valueOf(System.currentTimeMillis() / 1000); // Returns epoch in seconds.
		fields.put("TimeStamp", epoch);
		// 將當下的時間轉為UnixTimeStamp(見範例)用於驗證此次介接的時間區間。
		// 綠界驗證時間區間暫訂為 3 分鐘內有效，超過則此次介接無效。
		String checkMac = null;
		try {
			checkMac = ECPAY_UTILS.genCheckMacValue(fields);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		fields.put("CheckMacValue", checkMac);

		return fields;
	}

	// used by sending ECPay for the first time
	private Map<String, String> fieldsBuilder(String merchantTradeNo, String merchantTradeDate, Integer totalAmount,
			String itemName) {
		Map<String, String> fields = new HashMap<>();

		// merchantTradeNo, merchantTradeDate, ItemName 為動態拼，其他在 demo 環境下固定
		// 此處 3 種連結，全都是 Page Controller 去接 -> forward, redirect, text/html 回傳
		fields.put("MerchantID", ECPAY_PROPS.merchantId()); // 測試用，寫死
		fields.put("MerchantTradeNo", merchantTradeNo);
		fields.put("MerchantTradeDate", merchantTradeDate);
		fields.put("PaymentType", ECPAY_PROPS.paymentType()); // 測試用，寫死
		fields.put("TotalAmount", String.valueOf(totalAmount));
		fields.put("TradeDesc", ECPAY_PROPS.tradeDesc()); // 測試用，寫死
		fields.put("ItemName", itemName); // 限制 400 字
		fields.put("ReturnURL", ECPAY_PROPS.returnUrl());
		fields.put("ChoosePayment", ECPAY_PROPS.choosePayment()); // 測試用，寫死
		fields.put("EncryptType", ECPAY_PROPS.encryptType()); // 測試用，寫死
//		fields.put("ItemURL", ...); // 商品銷售網址

		// 以下兩者皆設定為佳（ClientBackURL 按鈕式，作為 OrderResultURL rollback 用）
		fields.put("ClientBackURL", ECPAY_PROPS.clientBackUrlPrefix() + merchantTradeNo);
//		fields.put("ClientBackURL", PROXY_HOST + "/front-end/order/ClientBackURL?merchantTradeNo=" + merchantTradeNo);
		fields.put("OrderResultURL", ECPAY_PROPS.orderResultUrlPrefix() + merchantTradeNo);
//		fields.put("OrderResultURL", PROXY_HOST + "/front-end/order/OrderResultURL?merchantTradeNo=" + merchantTradeNo);

		/********** 產生 CheckMacValue 1. 這段只能在後端 2. 要在所有欄位拼完後 **********/
		String checkMac = null;
		try {
			checkMac = ECPAY_UTILS.genCheckMacValue(fields);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		fields.put("CheckMacValue", checkMac);

		return fields;
	}

}
