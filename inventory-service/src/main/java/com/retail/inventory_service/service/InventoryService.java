package com.retail.inventory_service.service;


import com.retail.inventory_service.dto.InventoryRequest;
import com.retail.inventory_service.dto.InventoryResponse;
import com.retail.inventory_service.entity.Inventory;
import com.retail.inventory_service.exception.DuplicateInventoryException;
import com.retail.inventory_service.exception.InventoryNotFoundException;
import com.retail.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse createInventory(InventoryRequest request) {

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new DuplicateInventoryException(request.getProductId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(0)
                .status("ACTIVE")
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    public InventoryResponse getInventoryByProductId(String productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        return mapToResponse(inventory);
    }

    public InventoryResponse updateStock(String productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.setAvailableQuantity(quantity);

        Inventory updated = inventoryRepository.save(inventory);

        return mapToResponse(updated);
    }

    private InventoryResponse mapToResponse(Inventory inventory) {

        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .status(inventory.getStatus())
                .build();
    }
    public void reduceStock(
            String productId,
            Integer orderedQuantity) {

        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(
                                () -> new InventoryNotFoundException(productId)
                        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity()
                        - orderedQuantity
        );

        inventoryRepository.save(inventory);

        log.info(
                "Stock Updated Product={} Remaining={}",
                productId,
                inventory.getAvailableQuantity()
        );
    }
}