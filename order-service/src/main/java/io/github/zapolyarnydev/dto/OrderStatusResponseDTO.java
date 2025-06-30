package io.github.zapolyarnydev.dto;

import io.github.zapolyarnydev.entity.OrderStatus;

public record OrderStatusResponseDTO(OrderStatus orderStatus, String reason) {
}
