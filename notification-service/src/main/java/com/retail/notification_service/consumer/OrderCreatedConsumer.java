package com.retail.notification_service.consumer;


import com.retail.notification_service.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @KafkaListener(
            topics = "${app.kafka.topics.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        log.info(
                "OrderCreatedEvent received. orderId={}, productId={}, quantity={}, status={}",
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity(),
                event.getStatus()
        );

        log.info(
                "Notification sent successfully for orderId={}",
                event.getOrderId()
        );
    }
}
