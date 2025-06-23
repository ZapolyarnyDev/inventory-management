package io.github.zapolyarnydev.controller;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.service.OrderPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderPlaceService orderPlaceService;

    @PostMapping("/test")
    public ResponseEntity<String> placeOrder() {
        var testItems = List.of(
                new OrderItemDTO(UUID.randomUUID(), 2),
                new OrderItemDTO(UUID.randomUUID(), 34)
        );

        orderPlaceService.placeOrder(testItems, LocalDateTime.now());
        return ResponseEntity.ok("Send place order event!");
    }
}
