package io.github.zapolyarnydev.event.order;

import io.github.zapolyarnydev.dto.OrderItemEventDTO;

import java.util.List;
import java.util.UUID;

public record OrderCancelEvent(UUID orderId, List<OrderItemEventDTO> cancelledItems) {
}
