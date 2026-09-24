package com.shopmart.payment.repository;

import com.shopmart.payment.entity.Payment;
import com.shopmart.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findFirstByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
