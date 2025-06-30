package io.github.zapolyarnydev.event.order;

import io.github.zapolyarnydev.dto.OrderItemEventDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, List<OrderItemEventDTO> orderItems, LocalDateTime releaseTime) {
}
