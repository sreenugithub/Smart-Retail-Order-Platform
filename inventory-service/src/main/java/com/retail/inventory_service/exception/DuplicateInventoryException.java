package com.retail.inventory_service.exception;


public class DuplicateInventoryException extends RuntimeException {

    public DuplicateInventoryException(String productId) {
        super("Inventory already exists for productId: " + productId);
    }
}