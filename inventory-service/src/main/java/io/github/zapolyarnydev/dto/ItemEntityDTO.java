package io.github.zapolyarnydev.dto;

import java.util.UUID;

public record ItemEntityDTO(UUID uuid, String name, int quantity) {
}
