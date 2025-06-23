package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.entity.OrderEntity;
import io.github.zapolyarnydev.entity.OrderItemEntity;
import io.github.zapolyarnydev.event.order.OrderCancelEvent;
import io.github.zapolyarnydev.event.order.OrderPlacedEvent;
import io.github.zapolyarnydev.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPlaceService {

    private final OrderRepository repository;

    private final KafkaTemplate<String, Object> orderTemplate;

    @Transactional
    public void placeOrder(List<OrderItemDTO> orderItems, LocalDateTime releaseData) {
        var orderItemEntities = orderItems.stream()
                .map(dto -> new OrderItemEntity(dto.inventoryItemId(), dto.quantity()))
                .toList();

        var entity = new OrderEntity(orderItemEntities, releaseData);
        repository.save(entity);

        var event = new OrderPlacedEvent(entity.getUuid(), orderItems);
        orderTemplate.send("orders.placed", event);
    }

    @NotNull
    public OrderEntity findOrderEntity(UUID orderId) throws EntityNotFoundException {
        return repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with UUID: " + orderId + " not found"));
    }

    @Transactional
    public void cancelOrder(UUID orderId){
        var order = findOrderEntity(orderId);

        var itemsDto = order.getOrderItems().stream()
                .map(orderItemEntity -> new OrderItemDTO(orderItemEntity.getItemId(), orderItemEntity.getQuantity()))
                .toList();

        var event = new OrderCancelEvent(orderId, itemsDto);

        orderTemplate.send("orders.cancelled", event);
        orderTemplate.flush();

        repository.delete(order);
    }
}
