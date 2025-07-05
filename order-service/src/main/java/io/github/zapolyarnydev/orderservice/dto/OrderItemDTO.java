package io.github.zapolyarnydev.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderItemDTO (
        @NotNull UUID inventoryItemId,
        @Min(1) int quantity) {

}
