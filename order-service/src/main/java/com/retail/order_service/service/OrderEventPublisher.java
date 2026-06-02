package com.retail.order_service.service;

import com.retail.order_service.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.topics.order-created}")
    private String orderCreatedTopic;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {

        log.info("Publishing OrderCreatedEvent for orderId={}", event.getOrderId());

        kafkaTemplate.send(
                orderCreatedTopic,
                event.getOrderId(),
                event
        );
    }
}