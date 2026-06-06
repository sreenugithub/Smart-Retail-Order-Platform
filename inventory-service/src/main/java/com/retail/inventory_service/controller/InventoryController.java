package com.retail.inventory_service.controller;


import com.retail. inventory_service.dto.InventoryRequest;
import com.retail. inventory_service.dto.InventoryResponse;
import com.retail. inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @RequestBody InventoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable String productId) {

        log.info("Inventory request received for productId={}", productId);

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId)
        );
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable String productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.updateStock(productId, quantity)
        );
    }
}