package io.github.zapolyarnydev.inventoryservice.orderservice.controller;

import io.github.zapolyarnydev.inventoryservice.orderservice.controller.response.ApiResponse;
import io.github.zapolyarnydev.inventoryservice.orderservice.dto.OrderInfoResponseDTO;
import io.github.zapolyarnydev.inventoryservice.orderservice.dto.OrderStatusResponseDTO;
import io.github.zapolyarnydev.inventoryservice.orderservice.dto.PlaceOrderDTO;
import io.github.zapolyarnydev.inventoryservice.orderservice.dto.mapper.OrderMapper;
import io.github.zapolyarnydev.inventoryservice.orderservice.service.OrderPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderPlaceService orderPlaceService;

    private final OrderMapper orderMapper;

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<UUID>> placeOrder(@Valid @RequestBody PlaceOrderDTO placeOrderDTO) {
        var entity = orderPlaceService.createOrder(placeOrderDTO.orderItems(), placeOrderDTO.releaseDateTime());

        var apiResponse = new ApiResponse<>(true, "Order is being validated", entity.getUuid());

        return ResponseEntity.status(202).body(apiResponse);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable UUID orderId) {
        orderPlaceService.cancelOrder(orderId);
        var apiResponse = new ApiResponse<>(true, "Order deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderInfoResponseDTO>> getOrder(@PathVariable UUID orderId) {
        var entity = orderPlaceService.findOrderEntity(orderId);
        var responseDTO = orderMapper.toDTO(entity);
        var apiResponse = new ApiResponse<>(true, "Order info", responseDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<OrderStatusResponseDTO>> getOrderStatus(@PathVariable UUID orderId) {
        var entity = orderPlaceService.findOrderEntity(orderId);

        var responseDTO = new OrderStatusResponseDTO(entity.getOrderStatus(), entity.getStatusReason());
        var apiResponse = new ApiResponse<>(true, "Order info", responseDTO);
        return ResponseEntity.ok(apiResponse);
    }
}
