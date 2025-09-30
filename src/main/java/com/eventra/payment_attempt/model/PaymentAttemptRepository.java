package com.eventra.payment_attempt.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.order.model.OrderVO;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttemptVO, Integer>{

	Optional<PaymentAttemptVO> findByProviderOrderId(String providerOrderId);
	
//	Optional<PaymentAttemptVO> findByProviderTransactionId(String providerTransactionId);
	
	// scheduler -> 且有建立索引了
	@Query(value="select pa from PaymentAttemptVO pa where pa.createdAt < :threshold and "
			+ "pa.paymentAttemptStatus = 'pending'")
	List<PaymentAttemptVO> findExpiredPaymentAttempts(@Param("threshold") Timestamp threshold);
	
	Optional<PaymentAttemptVO> findTopByOrderIdOrderByCreatedAtDesc(Integer orderId);
	
//	Optional<PaymentAttemptVO> findByOrderIdAndPaymentAttemptStatus(Integer orderId, PaymentAttemptStatus status);
}
