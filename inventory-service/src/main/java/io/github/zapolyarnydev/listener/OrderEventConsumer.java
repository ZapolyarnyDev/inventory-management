package io.github.zapolyarnydev.listener;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.event.inventory.OrderStatusResponseEvent;
import io.github.zapolyarnydev.event.order.OrderCancelEvent;
import io.github.zapolyarnydev.event.order.OrderPlacedEvent;
import io.github.zapolyarnydev.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> inventoryTemplate;

    @KafkaListener(topics = "orders.placed")
    public void onPlaceOrder(OrderPlacedEvent placedEvent) {
        List<OrderItemDTO> items = placedEvent.orderItems();
        if(inventoryService.canReserve(placedEvent.orderItems())){
            inventoryService.reserveItems(items);

            var event = new OrderStatusResponseEvent(placedEvent.orderId(), true, "Заказ успешно зарезервирован");

            inventoryTemplate.send("inventory.order-status-response", event);
        } else {
            var event = new OrderStatusResponseEvent(placedEvent.orderId(), false, "Не хватает предметов для принятия заказа");

            inventoryTemplate.send("inventory.order-status-response", event);
        }
    }

    @KafkaListener(topics = "orders.cancelled")
    public void onOrderCancel(OrderCancelEvent cancelEvent){
        inventoryService.recoverItems(cancelEvent.cancelledItems());
    }
}
