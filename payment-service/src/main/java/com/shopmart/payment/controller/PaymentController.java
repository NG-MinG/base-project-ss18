package com.shopmart.payment.controller;

import com.shopmart.payment.dto.PaymentRequest;
import com.shopmart.payment.dto.PaymentResponse;
import com.shopmart.payment.dto.WalletResponse;
import com.shopmart.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(request);
    }

    @PostMapping("/refund/{orderId}")
    public PaymentResponse refund(@PathVariable Long orderId) {
        return paymentService.refund(orderId);
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> getByOrder(@PathVariable Long orderId) {
        return paymentService.findByOrderId(orderId);
    }

    @GetMapping("/wallets/{userId}")
    public WalletResponse getWallet(@PathVariable Long userId) {
        return paymentService.getWalletInfo(userId);
    }
}
