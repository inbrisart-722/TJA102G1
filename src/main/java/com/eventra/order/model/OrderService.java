package com.eventra.order.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.order.ecpay.model.ECPayService;
import com.eventra.order.linepay.model.LinePayService;
import com.eventra.order_item.model.OrderItemVO;
import com.eventra.payment_attempt.model.PaymentAttemptRepository;
import com.eventra.payment_attempt.model.PaymentAttemptVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class OrderService {

	@PersistenceContext
	private EntityManager entityManager;
	
	private final Long ORDER_EXPIRATION_MILLIS;
	private final Long PAYMENT_ATTEMPT_EXPIRATION_MILLIS;

	private final ExhibitionRepository EXHIBITION_REPO;
	private final OrderRepository ORDER_REPO;
	private final PaymentAttemptRepository PAYMENT_ATTEMPT_REPO;

	private final ECPayService ECPAY_SERVICE;
	private final LinePayService LINE_PAY_SERVICE;

	public OrderService(ExhibitionRepository exhibitionRepository,
			OrderRepository orderRepository, PaymentAttemptRepository paymentAttemptRepository,
			ECPayService ecpayService, 
			LinePayService linePayService, @Value("${order.expiration-millis}") Long orderExpirationMillis, @Value("${payment.attempt.expiration-millis}") Long paymentAttemptExpirationMillis) {
		this.EXHIBITION_REPO = exhibitionRepository;
		this.ORDER_REPO = orderRepository;
		this.PAYMENT_ATTEMPT_REPO = paymentAttemptRepository;
		this.ECPAY_SERVICE = ecpayService;
		this.LINE_PAY_SERVICE = linePayService;
		this.ORDER_EXPIRATION_MILLIS = orderExpirationMillis;
		this.PAYMENT_ATTEMPT_EXPIRATION_MILLIS = paymentAttemptExpirationMillis;
	}

	public OrderProvider getOrderProvider(String orderUlid) {
		return ORDER_REPO.findByOrderUlid(orderUlid).getOrderProvider();
	}
	
	public OrderVO getOneOrderByProviderOrderId(String providerOrderId) {
		return PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElseThrow().getOrder();
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
//		System.out.println(
//				"clearing pending payment attempts " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		/* ========== 1st part : 找到所有 1) 40 分鐘以上 2) pending 的 payment attempts ========== */
		Timestamp threshold = new Timestamp(System.currentTimeMillis() - PAYMENT_ATTEMPT_EXPIRATION_MILLIS); // 40mins
		List<PaymentAttemptVO> expiredPaymentAttempts = PAYMENT_ATTEMPT_REPO.findExpiredPaymentAttempts(threshold);
		for (PaymentAttemptVO vo : expiredPaymentAttempts) {
			if (OrderProvider.ECPay == vo.getProvider()) ECPAY_SERVICE.cleanExpiredECPayPaymentAttempt(vo);
			else if (OrderProvider.LINEPay == vo.getProvider()) LINE_PAY_SERVICE.cleanExpiredLinePayPaymentAttempt(vo);
		}
	}

	// ECPay 與 LinePay 直接通用，不用多寫邏輯
	public void clearExpiredOrders() {
//		System.out.println("clearing expired + pending orders... "
//				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
		/* ========== 1st part : 找到所有 1) 120 分鐘以上 2) 付款未成功("付款中" + "付款失敗") 3) 沒有任何 paymentAttempt status = "pending" 的 訂單 ========== */
		Timestamp threshold = new Timestamp(System.currentTimeMillis() - ORDER_EXPIRATION_MILLIS); // 120mins
		List<OrderVO> expiredOrders = ORDER_REPO.findExpiredOrders(threshold,
				Set.of(OrderStatus.付款中, OrderStatus.付款失敗));
		/* ********* 2rd part : 調整訂單狀態 order orderStatus -> 付款逾時 ********* */
		for (OrderVO vo : expiredOrders)
			vo.setOrderStatus(OrderStatus.付款逾時);
		/* ********* 3rd part : 庫存釋放 ********* */
		Map<Integer, Integer> releaseMap = expiredOrders.stream().flatMap(order -> order.getOrderItems().stream())
				.collect(Collectors.groupingBy(item -> item.getExhibitionTicketType().getExhibitionId(),
						Collectors.summingInt(i -> 1)));
		for (Map.Entry<Integer, Integer> entry : releaseMap.entrySet())
			EXHIBITION_REPO.updateSoldTicketQuantity(entry.getKey(), -entry.getValue());
	}

	// 給 order_pending 頁面用
	// 不論是 ECPay or LINE Pay 都可能是 client 端先回來，server to server 的訂單狀態卻還沒更新完，所以先轉導
	// pending 後打 api 接此條 service method 根據實際訂單狀態再去轉導。
	public OrderStatus checkOrderStatus(String providerOrderId) {
		OrderStatus orderStatus = PAYMENT_ATTEMPT_REPO.findByProviderOrderId(providerOrderId).orElseThrow().getOrder()
				.getOrderStatus();
		return orderStatus;
	}
}
