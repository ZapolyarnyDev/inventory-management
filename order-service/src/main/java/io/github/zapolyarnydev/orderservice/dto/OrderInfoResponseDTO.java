package io.github.zapolyarnydev.orderservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderInfoResponseDTO(UUID uuid, List<OrderItemDTO> orderItems,
                                   LocalDateTime createDateTime, LocalDateTime releaseDateTime) {
}
