package com.shopmart.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long orderId,
        @NotNull Long userId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount) {
}
