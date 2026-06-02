package com.retail.order_service.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private String orderId;

    private String productId;

    private Integer quantity;

    private String status;
}