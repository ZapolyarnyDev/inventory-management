package io.github.zapolyarnydev.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PlaceOrderDTO(
        @NotNull @NotEmpty List<@Valid OrderItemDTO> orderItems,
        @NotNull @Future LocalDateTime releaseDateTime
        ) {
}
