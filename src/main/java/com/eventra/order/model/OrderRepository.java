package com.eventra.order.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.member.model.MemberVO;

public interface OrderRepository extends JpaRepository<OrderVO, Integer> {

	// scheduler -> 且有建立索引了
	@Query(value = "select o from OrderVO o where o.createdAt < :threshold and o.orderStatus in (:statuses) "
			+ "and not exists (select 1 from o.paymentAttempts pa where pa.paymentAttemptStatus = 'pending')")
	List<OrderVO> findExpiredOrders(@Param("threshold") Timestamp threshold,
			@Param("statuses") Set<OrderStatus> statuses);

	List<OrderVO> findAllByMemberId(Integer memberId);

	OrderVO findByOrderUlid(String orderUlid);

	Slice<OrderVO> findByMemberAndOrderStatus(MemberVO member, OrderStatus orderStatus, Pageable pageable);

	// 後台展商今日新訂單數
	@Query("""
			select count(distinct o.orderId)
			from OrderVO o
				join o.orderItems oi
				join oi.exhibitionTicketType ett
				join ett.exhibition e
				join e.exhibitorVO ex
			where ex.exhibitorId = :exhibitorId
			and o.createdAt >= :start
			and o.createdAt < :end
			""")
	long countNewOrdersToday(@Param("exhibitorId") Integer exhibitorId, @Param("start") Timestamp start,
			@Param("end") Timestamp end);

	// 後台展商今日新訂單總額（找出屬於該展商的訂單後加總）
	@Query("""
			select coalesce(sum(o.totalAmount), 0)
			from OrderVO o
			where o.createdAt >= :start
			and o.createdAt < :end
			and exists(
			select 1
			from OrderItemVO oi
				join oi.exhibitionTicketType ett
				join ett.exhibition e
				join e.exhibitorVO ex
			where oi.order = o
				and ex.exhibitorId = :exhibitorId
				)
			""")

	long sumNewOrdersAmountToday(@Param("exhibitorId") Integer exhibitorId, @Param("start") Timestamp start,
			@Param("end") Timestamp end);

	// 訂單列表查詢
	@Query(value = """
			  select new com.eventra.order.model.OrderSummaryDTO(
			    o.orderId,
			    o.orderUlid,
			    coalesce(m.fullName, '-'),
			    o.orderStatus,
			    count(distinct oi.orderItemId),
			    o.totalAmount,
			    o.createdAt,
			    coalesce(max(e.exhibitionName), '-'),
			    coalesce(max(tt.ticketTypeName), '-')
			  )
			  from OrderVO o
			  left join o.member m
			  join o.orderItems oi
			  join oi.exhibitionTicketType ett
			  join ett.exhibition e
			  join e.exhibitorVO xb
			  join ett.ticketType tt
			  where xb.exhibitorId = :exhibitorId
			    and (:status is null or o.orderStatus = :status) 
			    and (
			      :q is null or :q = '' or
			      lower(o.orderUlid) like lower(concat('%', :q, '%')) or
			      lower(coalesce(m.fullName,'')) like lower(concat('%', :q, '%')) or
			      lower(coalesce(e.exhibitionName,'')) like lower(concat('%', :q, '%'))
			    )
			  group by o.orderId, o.orderUlid, m.fullName, o.orderStatus, o.totalAmount, o.createdAt
			  order by o.createdAt desc
			""", countQuery = """
			  select count(distinct o.orderId)
			  from OrderVO o
			  left join o.member m
			  join o.orderItems oi
			  join oi.exhibitionTicketType ett
			  join ett.exhibition e
			  join e.exhibitorVO xb
			  where xb.exhibitorId = :exhibitorId
			    and (:status is null or o.orderStatus = :status)
			    and (
			      :q is null or :q = '' or
			      lower(o.orderUlid) like lower(concat('%', :q, '%')) or
			      lower(coalesce(m.fullName,'')) like lower(concat('%', :q, '%')) or
			      lower(coalesce(e.exhibitionName,'')) like lower(concat('%', :q, '%'))
			    )
			""")
	Page<OrderSummaryDTO> findOrderSummaries(@Param("exhibitorId") Integer exhibitorId,
			@Param("status") com.eventra.order.model.OrderStatus status, @Param("q") String q, Pageable pageable);
}