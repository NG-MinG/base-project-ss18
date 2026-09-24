package com.shopmart.payment.config;

import com.shopmart.payment.entity.Wallet;
import com.shopmart.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        if (walletRepository.count() > 0) {
            return;
        }
        walletRepository.saveAll(List.of(
                Wallet.builder().userId(1L).balance(new BigDecimal("100000000")).build(),
                Wallet.builder().userId(2L).balance(new BigDecimal("5000000")).build()
        ));
        log.info("Seeded sample wallets (user 1: 100,000,000 | user 2: 5,000,000)");
    }
}
