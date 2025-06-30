package io.github.zapolyarnydev.listener;

import io.github.zapolyarnydev.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.event.inventory.OrderStatusResponseEvent;
import io.github.zapolyarnydev.event.order.OrderCancelEvent;
import io.github.zapolyarnydev.event.order.OrderPlacedEvent;
import io.github.zapolyarnydev.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> inventoryTemplate;

    @KafkaListener(topics = "orders.placed")
    public void onPlaceOrder(OrderPlacedEvent placedEvent) {
        List<OrderItemEventDTO> items = placedEvent.orderItems();
        try {
            if(inventoryService.canReserve(placedEvent.orderItems())){
                inventoryService.reserveItems(items);

                var event = new OrderStatusResponseEvent(placedEvent.orderId(), true, "Заказ успешно зарезервирован");

                inventoryTemplate.send("inventory.order-status-response", event);
            } else {
                placedEvent.orderItems().clear();
                var event = new OrderStatusResponseEvent(placedEvent.orderId(), false, "Не хватает предметов для принятия заказа");

                inventoryTemplate.send("inventory.order-status-response", event);
            }
        } catch (RuntimeException e) {
            placedEvent.orderItems().clear();
            var event = new OrderStatusResponseEvent(placedEvent.orderId(), false, e.getMessage());

            inventoryTemplate.send("inventory.order-status-response", event);
        }
    }


    @KafkaListener(topics = "orders.cancelled")
    public void onOrderCancel(OrderCancelEvent cancelEvent){
        inventoryService.recoverItems(cancelEvent.cancelledItems());
    }
}
