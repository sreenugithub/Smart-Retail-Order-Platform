package com.retail.order_service.exception;


public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productId) {
        super("Insufficient stock for productId: " + productId);
    }
}
