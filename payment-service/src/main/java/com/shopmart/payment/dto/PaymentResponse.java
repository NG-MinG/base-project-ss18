package com.shopmart.payment.dto;

import com.shopmart.payment.entity.Payment;
import com.shopmart.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Long id, Long orderId, Long userId, BigDecimal amount,
                              PaymentStatus status, LocalDateTime createdAt) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getUserId(), p.getAmount(),
                p.getStatus(), p.getCreatedAt());
    }
}
