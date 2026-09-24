package com.shopmart.order.service;

import com.shopmart.order.dto.CreateOrderRequest;
import com.shopmart.order.dto.UpdateStatusRequest;
import com.shopmart.order.entity.Order;
import com.shopmart.order.entity.OrderStatus;
import com.shopmart.order.exception.BusinessException;
import com.shopmart.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_statusPending() {
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        var res = orderService.createOrder(new CreateOrderRequest(1L, 1L, 2));

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void updateStatus_notPending_throws() {
        Order order = Order.builder().id(1L).userId(1L).productId(1L).quantity(1)
                .status(OrderStatus.CANCELLED).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L,
                new UpdateStatusRequest(OrderStatus.CONFIRMED, null)))
                .isInstanceOf(BusinessException.class);
    }
}
