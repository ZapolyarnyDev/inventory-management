package io.github.zapolyarnydev.inventoryservice.controller.response;

public record ApiResponse<T> (boolean success, String message, T data) {
}
