package com.shopmart.order.service;

import com.shopmart.order.dto.CreateOrderRequest;
import com.shopmart.order.dto.OrderResponse;
import com.shopmart.order.dto.UpdateStatusRequest;
import com.shopmart.order.entity.Order;
import com.shopmart.order.entity.OrderStatus;
import com.shopmart.order.exception.BusinessException;
import com.shopmart.order.exception.ResourceNotFoundException;
import com.shopmart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Tạo đơn hàng ở trạng thái PENDING.
     *
     * Base project CHƯA kết nối với inventory-service / payment-service.
     * TODO (Câu 2): gọi inventory-service qua FeignClient (lấy giá, trừ tồn kho) + Circuit Breaker.
     * TODO (Câu 3): phát sự kiện đặt hàng lên Kafka và xử lý Saga (thành công / compensating).
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(Order.builder()
                .userId(request.userId())
                .productId(request.productId())
                .quantity(request.quantity())
                .status(OrderStatus.PENDING)
                .build());
        log.info("Created order id={} (userId={}, productId={}, qty={}) status=PENDING",
                order.getId(), order.getUserId(), order.getProductId(), order.getQuantity());
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return OrderResponse.from(getEntity(id));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateStatusRequest request) {
        Order order = getEntity(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Chỉ cập nhật được đơn ở trạng thái PENDING (hiện tại: "
                    + order.getStatus() + ")");
        }
        order.setStatus(request.status());
        order.setNote(request.note());
        log.info("Order id={} changed status -> {}", id, request.status());
        return OrderResponse.from(order);
    }

    private Order getEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));
    }
}
