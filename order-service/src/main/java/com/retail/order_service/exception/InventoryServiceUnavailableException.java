package com.retail.order_service.exception;


public class InventoryServiceUnavailableException extends RuntimeException {

    public InventoryServiceUnavailableException() {
        super("Inventory service is currently unavailable");
    }
}