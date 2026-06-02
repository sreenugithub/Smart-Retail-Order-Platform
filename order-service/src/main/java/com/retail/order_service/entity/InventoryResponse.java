package com.retail.order_service.entity;

import lombok.Data;

@Data
public class InventoryResponse {

    private String productId;

    private String productName;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private String status;
}