package com.shopmart.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Ví tiền của khách hàng (dùng để mô phỏng thanh toán / hoàn tiền). */
@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
}
