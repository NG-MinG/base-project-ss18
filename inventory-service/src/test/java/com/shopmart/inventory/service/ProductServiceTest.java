package com.shopmart.inventory.service;

import com.shopmart.inventory.entity.Product;
import com.shopmart.inventory.exception.BusinessException;
import com.shopmart.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sample(int stock) {
        return Product.builder().id(1L).name("iPhone 15").price(new BigDecimal("100")).stock(stock).build();
    }

    @Test
    void deductStock_success() {
        Product p = sample(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        productService.deductStock(1L, 3);

        assertThat(p.getStock()).isEqualTo(7);
    }

    @Test
    void deductStock_insufficient_throws() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sample(2)));

        assertThatThrownBy(() -> productService.deductStock(1L, 5))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void restoreStock_addsBack() {
        Product p = sample(7);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        productService.restoreStock(1L, 3);

        assertThat(p.getStock()).isEqualTo(10);
    }
}
