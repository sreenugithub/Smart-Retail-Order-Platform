package com.retail.order_service.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private String productId;

    private Integer quantity;
}
