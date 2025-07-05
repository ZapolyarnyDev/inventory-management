package io.github.zapolyarnydev.orderservice.dto;

import io.github.zapolyarnydev.orderservice.entity.OrderStatus;

public record OrderStatusResponseDTO(OrderStatus orderStatus, String reason) {
}
