package com.retail.inventory_service.dto;


import lombok.Data;

@Data
public class InventoryRequest {

    private String productId;
    private String productName;
    private Integer availableQuantity;
}
