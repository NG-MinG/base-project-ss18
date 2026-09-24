package com.shopmart.inventory.config;

import com.shopmart.inventory.entity.Product;
import com.shopmart.inventory.repository.ProductRepository;
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

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(List.of(
                Product.builder().name("iPhone 15").price(new BigDecimal("22000000")).stock(10).build(),
                Product.builder().name("Samsung Galaxy S24").price(new BigDecimal("18000000")).stock(15).build(),
                Product.builder().name("Tai nghe AirPods Pro").price(new BigDecimal("5500000")).stock(30).build()
        ));
        log.info("Seeded sample products");
    }
}
