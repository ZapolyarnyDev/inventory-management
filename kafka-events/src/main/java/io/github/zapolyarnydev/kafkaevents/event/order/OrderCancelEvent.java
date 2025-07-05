package io.github.zapolyarnydev.kafkaevents.event.order;

import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;

import java.util.List;
import java.util.UUID;

public record OrderCancelEvent(UUID orderId, List<OrderItemEventDTO> cancelledItems) {
}
