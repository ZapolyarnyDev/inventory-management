package io.github.zapolyarnydev.dto;

import java.util.UUID;

public record OrderItemEventDTO(UUID inventoryItemId, int quantity) {

}
