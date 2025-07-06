package io.github.zapolyarnydev.orderservice.unit;

import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.orderservice.dto.OrderItemDTO;
import io.github.zapolyarnydev.orderservice.dto.mapper.OrderEventMapper;
import io.github.zapolyarnydev.orderservice.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование маппинга DTO предметов в DTO для событий в kafka")
public class OrderEventMapperTests {

    private final OrderEventMapper orderEventMapper = Mappers.getMapper(OrderEventMapper.class);

    private Random random;

    @BeforeEach
    public void createRandom() {
        random = new Random();
    }

    @Test
    @DisplayName("При маппинге сущности предмета заказа в DTO события для kafka данные должны быть сохранены")
    public void shouldMapOrderItemDTO_To_KafkaEventDTO() {
        var dto = new OrderItemDTO(UUID.randomUUID(), random.nextInt());

        OrderItemEventDTO itemEventDTO = orderEventMapper.toEventDto(dto);

        assertEquals(dto.inventoryItemId(), itemEventDTO.inventoryItemId());
        assertEquals(dto.quantity(), itemEventDTO.quantity());
    }

    @Test
    @DisplayName("При маппинге DTO события для kafka в сущность предмета заказа данные должны быть сохранены")
    public void shouldMapKafkaEventDTO_To_OrderItemDTO() {
        var dto = new OrderItemEventDTO(UUID.randomUUID(), random.nextInt());

        OrderItemDTO itemDTO = orderEventMapper.toItemDto(dto);

        assertEquals(dto.inventoryItemId(), itemDTO.inventoryItemId());
        assertEquals(dto.quantity(), itemDTO.quantity());
    }

    @Test
    @DisplayName("При маппинге списка сущностей предмета заказа в список DTO события для kafka данные должны быть сохранены")
    public void shouldMapOrderItemDTOList_To_KafkaEventDTOList() {
        List<OrderItemDTO> itemDTOList = getRandomOrderItemDTOList();

        List<OrderItemEventDTO> itemEventDTOList = orderEventMapper.toEventDto(itemDTOList);

        assertEquals(itemDTOList.size(), itemEventDTOList.size());

        for (int i = 0; i < itemDTOList.size(); i++) {
            assertEquals(itemDTOList.get(i).inventoryItemId(), itemEventDTOList.get(i).inventoryItemId());
            assertEquals(itemDTOList.get(i).quantity(), itemEventDTOList.get(i).quantity());
        }
    }

    @Test
    @DisplayName("При маппинге списка DTO события для kafka в список сущностей предмета заказа данные должны быть сохранены")
    public void shouldMapKafkaEventDTOList_To_OrderItemDTOList() {
        List<OrderItemEventDTO> itemEventDTOList = getRandomOrderItemEventDTOList();

        List<OrderItemDTO> itemDTOList = orderEventMapper.toItemDto(itemEventDTOList);

        assertEquals(itemEventDTOList.size(), itemDTOList.size());

        for (int i = 0; i < itemDTOList.size(); i++) {
            assertEquals(itemEventDTOList.get(i).inventoryItemId(), itemDTOList.get(i).inventoryItemId());
            assertEquals(itemEventDTOList.get(i).quantity(), itemDTOList.get(i).quantity());
        }
    }

    private List<OrderItemDTO> getRandomOrderItemDTOList() {
        return List.of(
                new OrderItemDTO(UUID.randomUUID(), 89231),
                new OrderItemDTO(UUID.randomUUID(), 1),
                new OrderItemDTO(UUID.randomUUID(), 8335)
        );
    }

    private List<OrderItemEventDTO> getRandomOrderItemEventDTOList() {
        return List.of(
                new OrderItemEventDTO(UUID.randomUUID(), 456),
                new OrderItemEventDTO(UUID.randomUUID(), 1),
                new OrderItemEventDTO(UUID.randomUUID(), 123134)
        );
    }
}
