package io.github.zapolyarnydev.controller.exception;

import io.github.zapolyarnydev.controller.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(EntityNotFoundException e){
        var response = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(404).body(response);
    }
}
