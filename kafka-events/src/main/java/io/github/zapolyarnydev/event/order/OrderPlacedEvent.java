package io.github.zapolyarnydev.event.order;

import io.github.zapolyarnydev.dto.OrderItemDTO;

import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, List<OrderItemDTO> orderItems) {
}
