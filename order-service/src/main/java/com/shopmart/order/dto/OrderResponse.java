package com.shopmart.order.dto;

import com.shopmart.order.entity.Order;
import com.shopmart.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(Long id, Long userId, Long productId, Integer quantity, BigDecimal totalAmount,
                            OrderStatus status, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getUserId(), o.getProductId(), o.getQuantity(),
                o.getTotalAmount(), o.getStatus(), o.getNote(), o.getCreatedAt(), o.getUpdatedAt());
    }
}
