package com.retail.inventory_service.exception;


public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String productId) {
        super("Inventory not found for productId: " + productId);
    }
}