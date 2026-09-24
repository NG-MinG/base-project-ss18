package com.shopmart.order.dto;

import com.shopmart.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull OrderStatus status, String note) {
}
