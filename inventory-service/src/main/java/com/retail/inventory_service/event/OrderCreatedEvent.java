package com.retail.inventory_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private String orderId;

    private String productId;

    private Integer quantity;

    private String status;

    private LocalDateTime orderDate;
}
