package io.github.zapolyarnydev.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegisterItemDTO(
        @NotNull String name,
        @Min(0) int quantity) {
}
