package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.OrderEntity;
import io.github.zapolyarnydev.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPlaceService {

    private OrderRepository repository;

    public void placeOrder(UUID inventoryItemId, int quantity) {

    }

    @NotNull
    public OrderEntity findOrderEntity(UUID orderId) throws EntityNotFoundException {
        return repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with UUID: " + orderId + " not found"));
    }

    @Transactional
    public void cancelOrder(UUID orderId){
        var order = findOrderEntity(orderId);
        repository.delete(order);
    }
}
