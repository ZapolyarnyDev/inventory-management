package io.github.zapolyarnydev.orderservice.unit;

import io.github.zapolyarnydev.kafkaevents.event.order.OrderCancelEvent;
import io.github.zapolyarnydev.orderservice.dto.OrderItemDTO;
import io.github.zapolyarnydev.orderservice.dto.mapper.OrderEventMapper;
import io.github.zapolyarnydev.orderservice.entity.OrderEntity;
import io.github.zapolyarnydev.orderservice.entity.OrderItemEntity;
import io.github.zapolyarnydev.orderservice.entity.OrderStatus;
import io.github.zapolyarnydev.orderservice.repository.OrderRepository;
import io.github.zapolyarnydev.orderservice.service.OrderPlaceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Работа сервиса создания заказов")
public class OrderPlaceServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> orderTemplate;

    @Mock
    private OrderEventMapper orderEventMapper;

    @InjectMocks
    private OrderPlaceService orderPlaceService;

    @Nested
    @DisplayName("Тестирование сохранения информации о заказанных предметах")
    public class savingOrderContentTests {

        @ParameterizedTest(name = "{index} - ожидаемое содержимое заказа не должно меняться")
        @DisplayName("При сохранении содержимое заказа не должно измениться")
        @MethodSource("provideOrderItemDTOList")
        public void shouldReturnOrderWithCorrectItemEntity(List<OrderItemDTO> itemDTOS) {
            var entities = getEntitiesFromDTO(itemDTOS);

            when(orderRepository.save(any())).thenReturn(new OrderEntity(entities, LocalDateTime.now()));

            var orderEntity = orderPlaceService.createOrder(itemDTOS, LocalDateTime.now());

            var entityOrderItems = orderEntity.getOrderItems();

            assertEquals(entityOrderItems.size(), itemDTOS.size());

            for (int i = 0; i < itemDTOS.size(); i++) {
                assertEquals(entityOrderItems.get(i).getItemId(), itemDTOS.get(i).inventoryItemId(), "Содержимое заказа N " + i + " не совпало");
                assertEquals(entityOrderItems.get(i).getQuantity(), itemDTOS.get(i).quantity(), "Содержимое заказа N " + i + " не совпало");
            }
        }

        private static Stream<Arguments> provideOrderItemDTOList() {
            return Stream.of(
                    Arguments.of(createRandomOrderItemDTOList(5)),
                    Arguments.of(createRandomOrderItemDTOList(12)),
                    Arguments.of(createRandomOrderItemDTOList(4)),
                    Arguments.of(createRandomOrderItemDTOList(1)),
                    Arguments.of(createRandomOrderItemDTOList(13)),
                    Arguments.of(createRandomOrderItemDTOList(26))
            );
        }
    }

    @Nested
    @DisplayName("Тестирование изменения состояния заказа")
    public class changeOrderStatusTests {

        @ParameterizedTest(name = "{index} - статус заказа должен быть ACCEPTED, содержимое остаться без изменений")
        @DisplayName("При сохранении содержимое заказа не должно измениться")
        @MethodSource("provideOrderItemDTOListAndReason")
        public void shouldReturnOrderWithAcceptedStatusAndCorrectItemEntity(List<OrderItemDTO> itemDTOS, String reason) {
            var entities = getEntitiesFromDTO(itemDTOS);

            when(orderRepository.save(any())).thenReturn(new OrderEntity(entities, LocalDateTime.now()));

            var orderEntity = orderPlaceService.createOrder(itemDTOS, LocalDateTime.now());

            var beforeAcceptingItems = new ArrayList<>(orderEntity.getOrderItems());

            orderPlaceService.acceptOrder(orderEntity, reason);

            assertEquals(orderEntity.getOrderStatus(), OrderStatus.ACCEPTED);
            assertEquals(orderEntity.getStatusReason(), reason);

            var afterAcceptingItems = orderEntity.getOrderItems();

            assertTrue(beforeAcceptingItems.containsAll(afterAcceptingItems));
        }

        @ParameterizedTest(name = "{index} - статус заказа должен быть REJECTED, содержимое должно быть пустым")
        @DisplayName("При сохранении содержимое заказа должно быть удалено")
        @MethodSource("provideOrderItemDTOListAndReason")
        public void shouldReturnOrderWithRejectedStatusAndClearedItemEntityList(List<OrderItemDTO> itemDTOS, String reason) {
            var entities = getEntitiesFromDTO(itemDTOS);

            when(orderRepository.save(any())).thenReturn(new OrderEntity(entities, LocalDateTime.now()));

            var orderEntity = orderPlaceService.createOrder(itemDTOS, LocalDateTime.now());

            orderPlaceService.rejectOrder(orderEntity, reason);

            assertEquals(orderEntity.getOrderStatus(), OrderStatus.REJECTED);
            assertEquals(orderEntity.getStatusReason(), reason);

            var afterAcceptingItems = orderEntity.getOrderItems();

            assertTrue(afterAcceptingItems.isEmpty());
        }

        private static Stream<Arguments> provideOrderItemDTOListAndReason() {
            return Stream.of(
                    Arguments.of(createRandomOrderItemDTOList(120), "Order Reason 1"),
                    Arguments.of(createRandomOrderItemDTOList(13), "Order Reason 2"),
                    Arguments.of(createRandomOrderItemDTOList(2), "Order Reason 3")
            );
        }
    }

    @Test
    @DisplayName("Отмена заказа должна вызывать удаление и отправку события в Kafka")
    public void shouldCancelOrderAndSendKafkaEvent() {
        UUID orderId = UUID.randomUUID();

        var orderItems = List.of(
                new OrderItemEntity(UUID.randomUUID(), 245),
                new OrderItemEntity(UUID.randomUUID(), 37)
        );

        var eventCaptor = ArgumentCaptor.forClass(OrderCancelEvent.class);

        var orderEntity = new OrderEntity(orderItems, LocalDateTime.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        orderPlaceService.cancelOrder(orderId);

        verify(orderTemplate).send(eq("orders.cancelled"), eventCaptor.capture());
        verify(orderTemplate).flush();

        OrderCancelEvent capturedEvent = eventCaptor.getValue();

        assertEquals(capturedEvent.orderId(), orderId);
        assertEquals(capturedEvent.cancelledItems().size(), 2);

        verify(orderRepository).delete(orderEntity);
    }

    @Test
    @DisplayName("Отмена несуществующего заказа должна выбрасывать EntityNotFoundException")
    public void shouldThrowExceptionWhenOrderNotFoundOnCancel() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderPlaceService.cancelOrder(orderId));
    }

    private static List<OrderItemDTO> createRandomOrderItemDTOList(int amount) {
        var random = new Random();

        List<OrderItemDTO> list = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            list.add(new OrderItemDTO(UUID.randomUUID(), random.nextInt(100)));
        }

        return list;
    }

    private List<OrderItemEntity> getEntitiesFromDTO(List<OrderItemDTO> orderItems) {
        return orderItems.stream()
                .map(dto -> new OrderItemEntity(dto.inventoryItemId(), dto.quantity()))
                .toList();
    }

}
