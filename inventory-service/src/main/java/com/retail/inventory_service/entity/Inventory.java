package com.retail.inventory_service.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    private String id;

    private String productId;
    private String productName;

    private Integer availableQuantity;
    private Integer reservedQuantity;

    private String status;
}