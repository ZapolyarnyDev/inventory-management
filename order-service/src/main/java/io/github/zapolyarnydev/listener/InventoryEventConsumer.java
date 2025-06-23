package io.github.zapolyarnydev.listener;

import io.github.zapolyarnydev.entity.OrderStatus;
import io.github.zapolyarnydev.event.inventory.OrderStatusResponseEvent;
import io.github.zapolyarnydev.service.OrderPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final OrderPlaceService orderPlaceService;
    private final KafkaTemplate<String, Object> inventoryTemplate;

    @KafkaListener(topics = "inventory.order-status-response")
    public void onPlaceOrder(OrderStatusResponseEvent event) {
        var order = orderPlaceService.findOrderEntity(event.orderId());

        if(event.isOrderPlaceAccepted()) {
            order.setOrderStatus(OrderStatus.ACCEPTED);
        } else {
            order.setOrderStatus(OrderStatus.REJECTED);
        }

        order.setStatusReason(event.reason());
    }
}
