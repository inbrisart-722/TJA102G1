package com.eventra.order.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderVO, Integer> {

	// scheduler -> 且有建立索引了
	@Query(value="select o from OrderVO o where o.createdAt < :threshold and o.orderStatus in (:statuses) "
			+ "and not exists (select 1 from o.paymentAttempts pa where pa.paymentAttemptStatus = 'pending')")
	List<OrderVO> findExpiredOrders(@Param("threshold") Timestamp threshold, @Param("statuses") Set<String> statuses);
	
	List<OrderVO> findAllByMemberId(Integer memberId);
}
