package com.shopmart.inventory.service;

import com.shopmart.inventory.dto.ProductRequest;
import com.shopmart.inventory.dto.ProductResponse;
import com.shopmart.inventory.entity.Product;
import com.shopmart.inventory.exception.BusinessException;
import com.shopmart.inventory.exception.ResourceNotFoundException;
import com.shopmart.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }

    // TODO (Câu 4): @Cacheable
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        log.info("Query product id={} from DB", id);
        return ProductResponse.from(getEntity(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product saved = productRepository.save(Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .build());
        log.info("Created product id={}", saved.getId());
        return ProductResponse.from(saved);
    }

    // TODO (Câu 4): @CachePut
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntity(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        log.info("Updated product id={}", id);
        return ProductResponse.from(product);
    }

    // TODO (Câu 4): @CacheEvict
    @Transactional
    public void delete(Long id) {
        productRepository.delete(getEntity(id));
        log.info("Deleted product id={}", id);
    }

    /** Trừ tồn kho khi đặt hàng. */
    @Transactional
    public ProductResponse deductStock(Long id, int quantity) {
        Product product = getEntity(id);
        if (product.getStock() < quantity) {
            log.error("Insufficient stock for product id={}: available={}, requested={}",
                    id, product.getStock(), quantity);
            throw new BusinessException("Không đủ tồn kho cho sản phẩm id=" + id);
        }
        product.setStock(product.getStock() - quantity);
        log.info("Deducted {} from product id={}, remaining={}", quantity, id, product.getStock());
        return ProductResponse.from(product);
    }

    /** Hoàn tồn kho (dùng cho bước compensating). */
    @Transactional
    public ProductResponse restoreStock(Long id, int quantity) {
        Product product = getEntity(id);
        product.setStock(product.getStock() + quantity);
        log.info("Restored {} to product id={}, remaining={}", quantity, id, product.getStock());
        return ProductResponse.from(product);
    }

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
    }
}
