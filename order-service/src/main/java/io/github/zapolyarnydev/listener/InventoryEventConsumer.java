package io.github.zapolyarnydev.listener;

import io.github.zapolyarnydev.dto.mapper.OrderEventMapper;
import io.github.zapolyarnydev.dto.mapper.OrderMapper;
import io.github.zapolyarnydev.entity.OrderStatus;
import io.github.zapolyarnydev.event.inventory.OrderStatusResponseEvent;
import io.github.zapolyarnydev.repository.OrderRepository;
import io.github.zapolyarnydev.service.OrderPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final OrderPlaceService orderPlaceService;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "inventory.order-status-response")
    public void onPlaceOrder(OrderStatusResponseEvent event) {
        var order = orderPlaceService.findOrderEntity(event.orderId());

        if(event.isOrderPlaceAccepted()) {
            orderPlaceService.acceptOrder(order, event.reason());
        } else {
           orderPlaceService.rejectOrder(order, event.reason());
        }
    }
}
