package com.retail.inventory_service.consumer;

import com.retail.inventory_service.event.OrderCreatedEvent;
import com.retail.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${app.kafka.topics.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            OrderCreatedEvent event) {

        log.info(
                "Inventory Consumer Received Order {}",
                event.getOrderId()
        );

        inventoryService.reduceStock(
                event.getProductId(),
                event.getQuantity()
        );
    }
}
