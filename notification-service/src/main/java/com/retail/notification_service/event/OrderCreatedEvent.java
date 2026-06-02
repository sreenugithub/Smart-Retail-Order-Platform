package com.retail.notification_service.event;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String orderId;
    private String productId;
    private Integer quantity;
    private String status;
    private LocalDateTime orderDate;
}
