package io.github.zapolyarnydev.dto;

import java.util.UUID;

public record OrderItemDTO(UUID inventoryItemId, int quantity) {

}
