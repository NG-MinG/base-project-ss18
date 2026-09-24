package com.shopmart.payment.service;

import com.shopmart.payment.dto.PaymentRequest;
import com.shopmart.payment.dto.PaymentResponse;
import com.shopmart.payment.dto.WalletResponse;
import com.shopmart.payment.entity.Payment;
import com.shopmart.payment.entity.PaymentStatus;
import com.shopmart.payment.entity.Wallet;
import com.shopmart.payment.exception.BusinessException;
import com.shopmart.payment.exception.ResourceNotFoundException;
import com.shopmart.payment.repository.PaymentRepository;
import com.shopmart.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    /** Thanh toán cho một đơn hàng: trừ tiền trong ví và ghi nhận payment. */
    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        Wallet wallet = getWallet(request.userId());

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            log.error("Payment failed for orderId={}: insufficient balance (balance={}, amount={})",
                    request.orderId(), wallet.getBalance(), request.amount());
            throw new BusinessException("Số dư không đủ để thanh toán đơn hàng id=" + request.orderId());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(request.orderId())
                .userId(request.userId())
                .amount(request.amount())
                .status(PaymentStatus.SUCCESS)
                .build());
        log.info("Payment SUCCESS orderId={}, amount={}, remainingBalance={}",
                request.orderId(), request.amount(), wallet.getBalance());
        return PaymentResponse.from(payment);
    }

    /** Hoàn tiền (dùng cho bước compensating). */
    @Transactional
    public PaymentResponse refund(Long orderId) {
        Payment payment = paymentRepository.findFirstByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không có giao dịch thành công cho đơn hàng id=" + orderId));

        Wallet wallet = getWallet(payment.getUserId());
        wallet.setBalance(wallet.getBalance().add(payment.getAmount()));
        payment.setStatus(PaymentStatus.REFUNDED);
        log.info("Payment REFUNDED orderId={}, amount={}", orderId, payment.getAmount());
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletInfo(Long userId) {
        Wallet wallet = getWallet(userId);
        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }

    private Wallet getWallet(Long userId) {
        return walletRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví của user id=" + userId));
    }
}
