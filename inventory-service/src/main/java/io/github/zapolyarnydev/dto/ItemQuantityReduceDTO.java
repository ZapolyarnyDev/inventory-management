package io.github.zapolyarnydev.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemQuantityReduceDTO(
       @NotNull UUID uuid,
       @Min(1) int quantity
) {
}
