package io.github.zapolyarnydev.inventoryservice.orderservice.dto;

import io.github.zapolyarnydev.inventoryservice.orderservice.entity.OrderStatus;

public record OrderStatusResponseDTO(OrderStatus orderStatus, String reason) {
}
