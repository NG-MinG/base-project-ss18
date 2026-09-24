package com.shopmart.payment.service;

import com.shopmart.payment.dto.PaymentRequest;
import com.shopmart.payment.entity.Payment;
import com.shopmart.payment.entity.PaymentStatus;
import com.shopmart.payment.entity.Wallet;
import com.shopmart.payment.exception.BusinessException;
import com.shopmart.payment.repository.PaymentRepository;
import com.shopmart.payment.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void pay_success_deductsBalance() {
        Wallet wallet = Wallet.builder().userId(1L).balance(new BigDecimal("1000")).build();
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        var res = paymentService.pay(new PaymentRequest(10L, 1L, new BigDecimal("300")));

        assertThat(res.status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(wallet.getBalance()).isEqualByComparingTo("700");
    }

    @Test
    void pay_insufficientBalance_throws() {
        Wallet wallet = Wallet.builder().userId(1L).balance(new BigDecimal("100")).build();
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> paymentService.pay(new PaymentRequest(10L, 1L, new BigDecimal("300"))))
                .isInstanceOf(BusinessException.class);
        assertThat(wallet.getBalance()).isEqualByComparingTo("100");
    }

    @Test
    void refund_restoresBalance() {
        Wallet wallet = Wallet.builder().userId(1L).balance(new BigDecimal("700")).build();
        Payment payment = Payment.builder().id(1L).orderId(10L).userId(1L)
                .amount(new BigDecimal("300")).status(PaymentStatus.SUCCESS).build();
        when(paymentRepository.findFirstByOrderIdAndStatus(10L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.of(payment));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        paymentService.refund(10L);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(wallet.getBalance()).isEqualByComparingTo("1000");
    }
}
