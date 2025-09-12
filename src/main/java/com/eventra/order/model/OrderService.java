package com.eventra.order.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.cart_item.model.CartItemRedisRepository;
import com.eventra.cart_item.model.CartItemRedisVO;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibition_ticket_type.model.ExhibitionTicketTypeVO;
import com.eventra.member.model.MemberVO;
import com.eventra.order_item.model.OrderItemRepository;
import com.eventra.order_item.model.OrderItemVO;
import com.eventra.payment_attempt.model.PaymentAttemptRepository;
import com.eventra.payment_attempt.model.PaymentAttemptVO;
import com.github.f4b6a3.ulid.UlidCreator;
import com.util.ECPayUtils;
import com.util.MerchantTradeNo36Generator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class OrderService {

	private static final String MERCHANT_ID = "2000132";
	private static final String PROXY_HOST = "https://259752055574.ngrok-free.app";

	@PersistenceContext
	private EntityManager entityManager;

	private ECPayUtils ECPAY_UTILS;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final CartItemRedisRepository CART_ITEM_REDIS_REPO;
	private final OrderRepository ORDER_REPO;
	private final OrderItemRepository ORDER_ITEM_REPO;
	private final PaymentAttemptRepository PAYMENT_ATTEMPT_REPO;

	public OrderService(ECPayUtils ECPayUtils, ExhibitionRepository exhibitionRepository,
			CartItemRedisRepository cartItemRedisRepository, OrderRepository orderRepository,
			OrderItemRepository orderItemRepository, PaymentAttemptRepository paymentAttemptRepository) {
		this.ECPAY_UTILS = ECPayUtils;
		this.EXHIBITION_REPO = exhibitionRepository;
		this.CART_ITEM_REDIS_REPO = cartItemRedisRepository;
		this.ORDER_REPO = orderRepository;
		this.ORDER_ITEM_REPO = orderItemRepository;
		this.PAYMENT_ATTEMPT_REPO = paymentAttemptRepository;
	}

	public OrderVO getOneOrderByTradeNo(String merchantTradeNo) {
		return PAYMENT_ATTEMPT_REPO.findByMerchantTradeNo(merchantTradeNo).orElseThrow().getOrder();
	}

	public List<GetAllOrderResDTO> getAllOrderByMemberId(Integer memberId) {
		// 會員中心用 一次拿前端再去按照頁籤切（三層架構...）
		List<OrderVO> orderVOs = ORDER_REPO.findAllByMemberId(memberId);
		List<GetAllOrderResDTO> resDTOs = new ArrayList<>();

		for (OrderVO oVO : orderVOs) {
			Map<ExhibitionVO, List<OrderItemVO>> map = oVO.getOrderItems().stream()
					.collect(Collectors.groupingBy(item -> item.getExhibitionTicketType().getExhibition()));
			List<GetAllOrderResGroupedDTO> groupedDTOs = new ArrayList<>();
			for (Map.Entry<ExhibitionVO, List<OrderItemVO>> entry : map.entrySet()) {
				/*
				 * ********* 1nd part : GetAllOrderResExhibitionDTO -> exhibitionName, location,
				 * startTime, endTime *********
				 */
				ExhibitionVO key = entry.getKey();
				GetAllOrderResExhibitionDTO eDTO = new GetAllOrderResExhibitionDTO()
						.setExhibitionName(key.getExhibitionName()).setLocation(key.getLocation())
						.setStartTime(key.getStartTime()).setEndTime(key.getEndTime());
				/*
				 * ********* 2nd part : GetAllOrderResOrderItemDTO -> orderItemUlid, ticketCode,
				 * ticketTypeName *********
				 */
				List<OrderItemVO> value = entry.getValue();
				List<GetAllOrderResOrderItemDTO> oiDTOs = new ArrayList<>();
				for (OrderItemVO el : value) {
					GetAllOrderResOrderItemDTO oiDTO = new GetAllOrderResOrderItemDTO()
							.setOrderItemUlid(el.getOrderItemUlid()).setTicketCode(el.getTicketCode())
							.setTicketTypeName(el.getExhibitionTicketType().getTicketType().getTicketTypeName());
					oiDTOs.add(oiDTO);
				}
				/*
				 * ********* 3nd part : GetAllOrderResGroupedDTO -> exhibitionDTO, orderItemsDTO
				 * *********
				 */
				GetAllOrderResGroupedDTO groupedDTO = new GetAllOrderResGroupedDTO().setExhibitionDTO(eDTO)
						.setOrderItemsDTO(oiDTOs);
				groupedDTOs.add(groupedDTO);
			}
			/*
			 * ********* 4st part : GetAllOrderResDTO -> orderUlid, orderStatus,
			 * totalAmount, totalQuantity, groups *********
			 */
			GetAllOrderResDTO resDTO = new GetAllOrderResDTO().setGroups(groupedDTOs)
					.setOrderStatus(oVO.getOrderStatus()).setOrderUlid(oVO.getOrderUlid())
					.setTotalAmount(oVO.getTotalAmount()).setTotalQuantity(oVO.getTotalQuantity());
			resDTOs.add(resDTO);
			/* ********* 5nd part : ********* */
		}
		return resDTOs;
	}

	public void clearExpiredPaymentAttempts() {
		System.out.println(
				"clearing pending payment attempts " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		/*
		 * ********* 1st part : 找到所有 1) 30 分鐘以上 2) pending 的 payment attempts *********
		 */
		Timestamp threshold = new Timestamp(System.currentTimeMillis() - 30 * 60 * 1000); // 30mins
		List<PaymentAttemptVO> expiredPaymentAttempts = PAYMENT_ATTEMPT_REPO.findExpiredPaymentAttempts(threshold);
		/* ********* 2rd part : 調整PA狀態 paymentOrderStatus -> expired ********* */
		for (PaymentAttemptVO vo : expiredPaymentAttempts) {
			vo.setPaymentAttemptStatus("expired");
			OrderVO orderVO = vo.getOrder();
			if (System.currentTimeMillis() - orderVO.getCreatedAt().getTime() < 60 * 60 * 1000) {
				orderVO.setOrderStatus("付款失敗");
				ORDER_REPO.save(orderVO);
			}
		}
	}

	public void clearExpiredOrders() {
		System.out.println("clearing expired + pending orders... "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		/*
		 * ********* 1st part : 找到所有 1) 60 分鐘以上 2) 付款未成功("待付款" + "付款失敗（未逾時）") 3) 沒有任何
		 * paymentAttempt status = "pending" 的 訂單 *********
		 */
		Timestamp threshold = new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000); // 60mins
		List<OrderVO> expiredOrders = ORDER_REPO.findExpiredOrders(threshold, Set.of("付款中", "付款失敗"));
		/* ********* 2rd part : 調整訂單狀態 order orderStatus -> 付款失敗 ********* */
		for (OrderVO vo : expiredOrders)
			vo.setOrderStatus("付款逾時");
		/* ********* 3rd part : 庫存釋放 ********* */
		Map<Integer, Integer> releaseMap = expiredOrders.stream().flatMap(order -> order.getOrderItems().stream())
				.collect(Collectors.groupingBy(item -> item.getExhibitionTicketType().getExhibitionId(),
						Collectors.summingInt(i -> 1)));
		// 若有改成計數版本，或想接 long 型態 ---> Collectors.summingInt(OrderItemVO::getQuantity) or
		// Collectors.counting();
		for (Map.Entry<Integer, Integer> entry : releaseMap.entrySet())
			EXHIBITION_REPO.updateSoldTicketQuantity(entry.getKey(), entry.getValue());
	}

	public String checkOrderStatus(String merchantTradeNo) {
		String orderStatus = PAYMENT_ATTEMPT_REPO.findByMerchantTradeNo(merchantTradeNo).orElseThrow().getOrder()
				.getOrderStatus();
		return orderStatus;
	}

	public String ECPayReturnURL(ECPayCallbackReqDTO req) {
		/* ********* 1st part : 從 merchantTradeNo 找到指定 PaymentAttempt ********* */
		String merchantTradeNo = req.getMerchantTradeNo();
		PaymentAttemptVO paVO = PAYMENT_ATTEMPT_REPO.findByMerchantTradeNo(merchantTradeNo).orElseThrow();
		/* ********* 2nd part : 核對 PaymentAttempt 送出前已存的部分欄位 失敗就在此截斷 0|FAIL ********* */
		System.out.println(req.toString());

		// 避免 nullpointer
		if (!Objects.equals(paVO.getMerchantId(), req.getMerchantID())
				|| !Objects.equals(paVO.getTradeAmt(), req.getTradeAmt()))
			return "0|failed";
		/* ********* 3rd part : 核對 CheckMacValue 失敗就在此截斷 0|FAIL ********* */
		// 自己拿 map 用我們手上的 HashKey 去算過
		String checkMacValue = null;
		try {
			checkMacValue = ECPAY_UTILS.genCheckMacValue(ECPAY_UTILS.genCheckMap(req));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		System.out.println("merchantCheckMacValue: " + req.getCheckMacValue());
		System.out.println("checkMacValue: " + checkMacValue);

		if (!Objects.equals(req.getCheckMacValue(), checkMacValue))
			return "0|failed";
		System.out.println("checkMacValue validated!!! it's ECPay here");
		/* ********* 4th part : 處理付款成功情況 ********* */
		if ("1".equals(req.getRtnCode())) {
			System.out.println("payment succeeded");
			/* ********* 4-1 part : 更新 PaymentAttempt 填入多項明細 ********* */
			paVO.setPaymentAttemptStatus("success");
			paVO.setStoreId(req.getStoreID());
			paVO.setTradeNo(req.getTradeNo());
			paVO.setRtnCode(req.getRtnCode());
			paVO.setRtnMsg(req.getRtnMsg());
			paVO.setSimulatePaid(req.getSimulatePaid());
			paVO.setPaymentType(req.getPaymentType());
			paVO.setPaymentTypeChargeFee(req.getPaymentTypeChargeFee());
			paVO.setPaymentDate(req.getPaymentDate());
			paVO.setCheckMacValue(req.getCheckMacValue());
			/* ********* 4-2 part : 更新 Order 調整 orderStatus ********* */
			OrderVO orderVO = paVO.getOrder();
			orderVO.setOrderStatus("已付款");
			ORDER_REPO.save(orderVO);
			/* ********* 4-3 part : 更新 OrderItem 寫入 ticketCode ********* */
			Set<OrderItemVO> orderItemVOs = orderVO.getOrderItems();
			for (OrderItemVO item : orderItemVOs)
				item.setTicketCode(genTicketCode(item.getOrderItemUlid()));
			/* ********* 4-4 part : 回傳 ECPay 1|OK ********* */
			System.out.println(1);
			return "1|OK";
		}
		/* ********* 5th part : 處理付款失敗與異常情況 ********* */
		else {
			System.out.println("payment failed");
			/* ********* 5-1 part : 更新 PaymentAttempt 填入多項明細 ********* */
			paVO.setPaymentAttemptStatus("failure");
			paVO.setStoreId(req.getStoreID());
			paVO.setTradeNo(req.getTradeNo());
			paVO.setRtnCode(req.getRtnCode());
			paVO.setRtnMsg(req.getRtnMsg());
			paVO.setSimulatePaid(req.getSimulatePaid());
			paVO.setPaymentType(req.getPaymentType());
			paVO.setPaymentTypeChargeFee(req.getPaymentTypeChargeFee());
			paVO.setPaymentDate(req.getPaymentDate());
			paVO.setCheckMacValue(req.getCheckMacValue());
			/* ********* 5-2 part : 額外確認此訂單是否過期 ********* */
			OrderVO orderVO = paVO.getOrder();
			Long createdAt = orderVO.getCreatedAt().getTime();
			if (System.currentTimeMillis() - createdAt > 60 * 60 * 1000) {
//				paVO.setPaymentAttemptStatus("expired");
				orderVO.setOrderStatus("付款逾時");
			}
			ORDER_REPO.save(orderVO);
			/* ********* 5-3 part : 回傳 ECPay 1|OK ********* */
			return "1|OK";
		}
		/* ********* 6th??? part : 處理付款異常情況 ********* */ // Could be Phase II???
		// 10300066：「交易付款結果待確認中，請勿出貨」，請至廠商管理後台確認已付款完成再出貨。
	}

	// 針對已建立之 order 重新送出金流
	public ECPaySendingResDTO ECPayResending(String orderUlid) {
		// 要確認訂單狀態是 "付款失敗" 才讓用戶送 送出當下要立即把訂單狀態調整成 "付款中"
		OrderVO orderVO = ORDER_REPO.findByOrderUlid(orderUlid);
		Integer orderId = orderVO != null ? orderVO.getOrderId() : null;
		if(orderId == null) return null; // 送錯或亂送 ulid 會造成此狀況！不要亂送！
		if(!"付款失敗".equals(orderVO.getOrderStatus())) return null; // 狀態不是指定狀態！不要亂送！
		
		orderVO.setOrderStatus("付款中");
		
		// no value present ？ 這裡 throw 會丟錯嗎 ？
		PaymentAttemptVO paVO = PAYMENT_ATTEMPT_REPO.findTopByOrderIdOrderByCreatedAtDesc(orderId).orElseThrow();
		Integer totalAmount = paVO.getTradeAmt();
		String itemName = paVO.getItemName();
		// 以下 2樣 都要重設重給（尤其是 merchantTradeNo 綠界會擋）
		String merchantTradeNo = MerchantTradeNo36Generator.generateMerchantTradeNo();
		String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		
		// 以下兩步驟 copy 第一次送出時的拼湊方式
		/* ********* 1th part : 拼湊前端所需資訊 ********* */
		// 除了 fields 要動態拼，其他在 demo 環境下為固定
		ECPaySendingResDTO res = new ECPaySendingResDTO.Builder().status("success")
				.action("https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5").method("POST")
				.fields(fieldsBuilder(merchantTradeNo, merchantTradeDate, totalAmount, itemName)).build();
		/* ********* 2th part : 新增 payment attempt 並回傳 ********* */
		// 需要 order, merchantTradeNo, merchantId, tradeAmt, paymentType, checkMacValue
		PaymentAttemptVO paymentAttemptVO = new PaymentAttemptVO.Builder().paymentAttemptStatus("pending")
				.order(orderVO).merchantTradeNo(merchantTradeNo).merchantId(MERCHANT_ID).tradeAmt(totalAmount)
//			.paymentType("Credit") // Callback 寫不一樣的...
				.tradeDate(merchantTradeDate).build();

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
		String merchantTradeNo = MerchantTradeNo36Generator.generateMerchantTradeNo();
		String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		// 綠界：送出時叫 merchantTradeDate => 回傳時叫 tradeDate
		System.out.println("before ulid");
		String ulid = UlidCreator.getUlid().toString();
		System.out.println("after ulid");

		/* ********* 2nd part : 新增 order ********* */
		// 需要 orderUlid, orderStatus, member, totalAmount, totalQuantity
		OrderVO orderVO = ORDER_REPO.save(new OrderVO.Builder().orderUlid(ulid).orderStatus("待付款")
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
		ECPaySendingResDTO res = new ECPaySendingResDTO.Builder().status("success")
				.action("https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5").method("POST")
				.fields(fieldsBuilder(merchantTradeNo, merchantTradeDate, totalAmount, itemName)).build();
		/* ********* 5th part : 新增 payment attempt 並回傳 ********* */
		// 需要 order, merchantTradeNo, merchantId, tradeAmt, paymentType, checkMacValue
		PaymentAttemptVO paymentAttemptVO = new PaymentAttemptVO.Builder().paymentAttemptStatus("pending")
				.order(orderVO).merchantTradeNo(merchantTradeNo).merchantId(MERCHANT_ID).tradeAmt(totalAmount)
//				.paymentType("Credit") // Callback 寫不一樣的...
				.tradeDate(merchantTradeDate).itemName(itemName).build();

		PAYMENT_ATTEMPT_REPO.save(paymentAttemptVO);

		return res;
	}

	private String genTicketCode(String ulid) {
		return "TK-" + ulid.substring(13, 20) + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-"
				+ ThreadLocalRandom.current().nextInt(1, 9999);
	}

	private Map<String, String> fieldsBuilder(String merchantTradeNo, String merchantTradeDate, Integer totalAmount,
			String itemName) {
		Map<String, String> fields = new HashMap<>();

		// merchantTradeNo, merchantTradeDate, ItemName 為動態拼，其他在 demo 環境下固定
		// 此處 3 種連結，全都是 Page Controller 去接 -> forward, redirect, text/html 回傳
		fields.put("MerchantID", MERCHANT_ID); // 測試用，寫死
		fields.put("MerchantTradeNo", merchantTradeNo);
		fields.put("MerchantTradeDate", merchantTradeDate);
		fields.put("PaymentType", "aio"); // 測試用，寫死
		fields.put("TotalAmount", String.valueOf(totalAmount));
		fields.put("TradeDesc", "Eventra demo  --- TradeDesc"); // 測試用，寫死
		fields.put("ItemName", itemName); // 限制 400 字
		fields.put("ReturnURL", PROXY_HOST + "/api/order/ECPay/ReturnURL");
		fields.put("ChoosePayment", "Credit"); // 測試用，寫死
		fields.put("EncryptType", "1"); // 測試用，寫死
//		fields.put("ItemURL", ...); // 商品銷售網址

		// 以下兩者皆設定為佳（ClientBackURL 按鈕式，作為 OrderResultURL rollback 用）
//		fields.put("ClientBackURL", "http://localhost:8088/order/ECPay/ClientBackURL");
		fields.put("ClientBackURL", PROXY_HOST + "/front-end/ClientBackURL?merchantTradeNo=" + merchantTradeNo);
//		fields.put("OrderResultURL", "http://localhost:8088/order/ECPay/OrderResultURL");
		fields.put("OrderResultURL", PROXY_HOST + "/front-end/OrderResultURL?merchantTradeNo=" + merchantTradeNo);

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
