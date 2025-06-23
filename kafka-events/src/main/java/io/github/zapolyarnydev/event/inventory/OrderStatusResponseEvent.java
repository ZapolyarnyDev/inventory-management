package io.github.zapolyarnydev.event.inventory;

import java.util.UUID;

public record OrderStatusResponseEvent(UUID orderId, boolean isOrderPlaceAccepted, String reason) {
}
