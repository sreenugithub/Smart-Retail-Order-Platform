package com.retail.order_service.service;

 
import com.retail.order_service.client.InventoryFeignClient;
import com.retail.order_service.dto.OrderCreatedEvent;
import com.retail.order_service.dto.OrderRequest;
import com.retail.order_service.entity.InventoryResponse;
import com.retail.order_service.entity.Order;
import com.retail.order_service.entity.OrderResponse;
import com.retail.order_service.exception.InsufficientStockException;
import com.retail.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryFeignClient inventoryFeignClient;
    private final OrderEventPublisher orderEventPublisher;

    @CircuitBreaker(name = "inventoryService")
    @Retry(name = "inventoryService", fallbackMethod = "inventoryFallback")
    public OrderResponse createOrder(OrderRequest request) {
        log.info(
                "Calling Inventory Service for product={}",
                request.getProductId()
        );
        InventoryResponse inventory =
                inventoryFeignClient.getInventory(request.getProductId());

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(request.getProductId());
        }

        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status("CREATED")
                .orderDate(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        orderEventPublisher.publishOrderCreatedEvent(
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getId())
                        .productId(savedOrder.getProductId())
                        .quantity(savedOrder.getQuantity())
                        .status(savedOrder.getStatus())
                        .orderDate(savedOrder.getOrderDate())
                        .build()

        );
        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .status(savedOrder.getStatus())
                .build();
    }

    public OrderResponse inventoryFallback(
            OrderRequest request,
            Exception ex) {
        log.error(
                "Inventory Service unavailable. Fallback triggered"
        );
        System.out.println("FALLBACK EXECUTED");
        System.out.println(ex.getClass().getName());

        return OrderResponse.builder()
                .orderId(null)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status("INVENTORY_SERVICE_UNAVAILABLE")
                .build();
    }
}
