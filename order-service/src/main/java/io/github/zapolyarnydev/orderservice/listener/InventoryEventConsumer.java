package io.github.zapolyarnydev.orderservice.listener;

import io.github.zapolyarnydev.kafkaevents.event.inventory.OrderStatusResponseEvent;
import io.github.zapolyarnydev.orderservice.repository.OrderRepository;
import io.github.zapolyarnydev.orderservice.service.OrderPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
