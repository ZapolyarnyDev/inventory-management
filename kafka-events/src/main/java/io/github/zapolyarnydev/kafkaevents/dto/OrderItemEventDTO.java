package io.github.zapolyarnydev.kafkaevents.dto;

import java.util.UUID;

public record OrderItemEventDTO(UUID inventoryItemId, int quantity) {

}
