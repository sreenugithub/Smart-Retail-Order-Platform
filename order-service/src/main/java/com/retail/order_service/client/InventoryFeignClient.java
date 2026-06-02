package com.retail.order_service.client;

import com.retail.order_service.entity.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "INVENTORY-SERVICE"
)
public interface InventoryFeignClient {

    @GetMapping("/api/inventory/{productId}")
    InventoryResponse getInventory(@PathVariable String productId
    );
}