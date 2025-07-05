package io.github.zapolyarnydev.inventoryservice.orderservice.controller.response;

public record ApiResponse <T> (boolean success, String message, T data) {
}
