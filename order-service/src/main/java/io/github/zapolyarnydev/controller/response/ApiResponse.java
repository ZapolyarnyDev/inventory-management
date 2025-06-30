package io.github.zapolyarnydev.controller.response;

public record ApiResponse <T> (boolean success, String message, T data) {
}
