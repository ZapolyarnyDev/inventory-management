package io.github.zapolyarnydev.inventoryservice.controller.exception;

import io.github.zapolyarnydev.inventoryservice.controller.response.ApiResponse;
import io.github.zapolyarnydev.inventoryservice.exception.ItemHasNameException;
import io.github.zapolyarnydev.inventoryservice.exception.SmallItemQuantityException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(ItemHasNameException.class)
    public ResponseEntity<ApiResponse<?>> handleHasName(ItemHasNameException e) {
        var response = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(EntityNotFoundException e) {
        var response = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(SmallItemQuantityException.class)
    public ResponseEntity<ApiResponse<?>> handleSmallItemQuantity(SmallItemQuantityException e) {
        var response = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(409).body(response);
    }
}
