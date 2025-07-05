package io.github.zapolyarnydev.orderservice.controller.response;

public record ApiResponse <T> (boolean success, String message, T data) {
}
