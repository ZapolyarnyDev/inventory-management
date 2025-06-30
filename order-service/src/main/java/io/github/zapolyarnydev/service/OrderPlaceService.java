package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.dto.PlaceOrderDTO;
import io.github.zapolyarnydev.dto.mapper.OrderEventMapper;
import io.github.zapolyarnydev.entity.OrderEntity;
import io.github.zapolyarnydev.entity.OrderItemEntity;
import io.github.zapolyarnydev.entity.OrderStatus;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPlaceService {

    private final OrderRepository repository;

    private final KafkaTemplate<String, Object> orderTemplate;

    private final OrderEventMapper eventMapper;

    @Transactional
    public OrderEntity createOrder(List<OrderItemDTO> orderItems, LocalDateTime releaseDateTime) {
        var orderItemEntities = orderItems.stream()
                .map(dto -> new OrderItemEntity(dto.inventoryItemId(), dto.quantity()))
                .toList();

        var entity = new OrderEntity(orderItemEntities, releaseDateTime);
        entity.setOrderStatus(OrderStatus.PENDING);
        entity.setStatusReason("Order is being validated");
        repository.save(entity);

        var eventDTOList = eventMapper.toEventDto(orderItems);

        var event = new OrderPlacedEvent(entity.getUuid(), eventDTOList, releaseDateTime);
        orderTemplate.send("orders.placed", event);

        return entity;
    }


    @NotNull
    public OrderEntity findOrderEntity(UUID orderId) throws EntityNotFoundException {
        return repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with UUID: " + orderId + " not found"));
    }

    @Transactional
    public void acceptOrder(OrderEntity order, String reason) {
        order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setStatusReason(reason);

        repository.save(order);
    }

    @Transactional
    public void rejectOrder(OrderEntity order, String reason) {
        order.setOrderStatus(OrderStatus.REJECTED);
        order.setStatusReason(reason);
        order.setOrderItems(Collections.emptyList());

        repository.save(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId) throws EntityNotFoundException{
        var order = findOrderEntity(orderId);

        var itemsDto = order.getOrderItems().stream()
                .map(orderItemEntity -> new OrderItemEventDTO(orderItemEntity.getItemId(), orderItemEntity.getQuantity()))
                .toList();

        var event = new OrderCancelEvent(orderId, itemsDto);

        orderTemplate.send("orders.cancelled", event);
        orderTemplate.flush();

        repository.delete(order);
    }
}
