package io.github.zapolyarnydev.kafkaevents.event.inventory;

import java.util.UUID;

public record OrderStatusResponseEvent(UUID orderId, boolean isOrderPlaceAccepted, String reason) {
}
