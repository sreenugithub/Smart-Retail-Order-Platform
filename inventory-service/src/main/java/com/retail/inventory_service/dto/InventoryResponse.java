package com.retail.inventory_service.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {

    private String productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private String status;
}