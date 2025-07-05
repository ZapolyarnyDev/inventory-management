package io.github.zapolyarnydev.inventoryservice.controller;

import io.github.zapolyarnydev.inventoryservice.controller.response.ApiResponse;
import io.github.zapolyarnydev.inventoryservice.dto.ItemEntityDTO;
import io.github.zapolyarnydev.inventoryservice.dto.ItemQuantityReduceDTO;
import io.github.zapolyarnydev.inventoryservice.dto.ItemReplenishDTO;
import io.github.zapolyarnydev.inventoryservice.dto.RegisterItemDTO;
import io.github.zapolyarnydev.inventoryservice.dto.mapper.InventoryItemEntityMapper;
import io.github.zapolyarnydev.inventoryservice.orderservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryItemEntityMapper entityMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ItemEntityDTO>> registerInventoryItem(@Valid @RequestBody RegisterItemDTO registerItemDTO) {
        var entity = inventoryService.addItem(registerItemDTO.name(), registerItemDTO.quantity());

        String message = String.format("The item %s was successfully added to the inventory", registerItemDTO.name());
        var apiResponse = new ApiResponse<>(true, message, entityMapper.toDTO(entity));
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/replenish")
    public ResponseEntity<ApiResponse<?>> replenishInventoryItem(@Valid @RequestBody ItemReplenishDTO replenishDTO) {
        inventoryService.increaseItemQuantity(replenishDTO.uuid(), replenishDTO.quantity());

        var apiResponse = new ApiResponse<>(true, "The item was successfully replenished", null);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/reduce")
    public ResponseEntity<ApiResponse<?>> decreaseInventoryItemQuantity(@Valid @RequestBody ItemQuantityReduceDTO quantityDecreaseDTO) {
        inventoryService.decreaseItemQuantity(quantityDecreaseDTO.uuid(), quantityDecreaseDTO.quantity());

        String message = String.format("The number of items has been successfully reduced by %d", quantityDecreaseDTO.quantity());
        var apiResponse = new ApiResponse<>(true, message, null);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/id/{inventoryItemId}")
    public ResponseEntity<ApiResponse<ItemEntityDTO>> getInventoryItemInfo(@PathVariable UUID inventoryItemId) {
        var entity = inventoryService.findItemEntity(inventoryItemId);

        var entityDTO = entityMapper.toDTO(entity);
        var apiResponse = new ApiResponse<>(true, "The item was successfully found", entityDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/name/{inventoryItemName}")
    public ResponseEntity<ApiResponse<ItemEntityDTO>> getInventoryItemInfo(@PathVariable String inventoryItemName) {
        var entity = inventoryService.findItemEntity(inventoryItemName);

        var entityDTO = entityMapper.toDTO(entity);
        var apiResponse = new ApiResponse<>(true, "The item was successfully found", entityDTO);
        return ResponseEntity.ok(apiResponse);
    }

}
