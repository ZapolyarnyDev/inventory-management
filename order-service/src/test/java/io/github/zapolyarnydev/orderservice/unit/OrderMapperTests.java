package io.github.zapolyarnydev.orderservice.unit;

import io.github.zapolyarnydev.orderservice.dto.OrderInfoResponseDTO;
import io.github.zapolyarnydev.orderservice.dto.OrderItemDTO;
import io.github.zapolyarnydev.orderservice.dto.mapper.OrderEventMapper;
import io.github.zapolyarnydev.orderservice.dto.mapper.OrderMapper;
import io.github.zapolyarnydev.orderservice.entity.OrderEntity;
import io.github.zapolyarnydev.orderservice.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование маппинга сущностей связанных с заказами")
public class OrderMapperTests {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @ParameterizedTest(name = "{index} - Исходные uuid: {0}, количество предметов {1}")
    @DisplayName("При маппинге сущности предмета заказа в DTO данные должны быть сохранены")
    @MethodSource("mappingOrderItemEntity_To_OrderItemDTOArgs")
    public void shouldMapOrderItemEntity_To_OrderItemDTOList(UUID uuid, int quantity) {
        var entity = new OrderItemEntity(uuid, quantity);

        OrderItemDTO dto = orderMapper.toDTO(entity);

        assertEquals(entity.getItemId(), dto.inventoryItemId());
        assertEquals(entity.getQuantity(), dto.quantity());
    }

    @Test
    @DisplayName("При маппинге списка сущностей предмета заказа в DTO данные должны быть сохранены")
    public void shouldMapOrderItemEntityList_To_OrderItemListDTO() {
        var entities = getRandomOrderItemEntityList();
        List<OrderItemDTO> mappedDTO = orderMapper.toItemDtoList(entities);

        assertEquals(entities.size(), mappedDTO.size());

        for (int i = 0; i < entities.size(); i++) {
            assertEquals(entities.get(i).getItemId(), mappedDTO.get(i).inventoryItemId());
            assertEquals(entities.get(i).getQuantity(), mappedDTO.get(i).quantity());
        }
    }

    @Test
    @DisplayName("При маппинге сущности заказа в DTO данные должны быть сохранены")
    public void shouldMapOrderEntity_To_OrderInfoResponseDTO() {
        UUID uuid = UUID.randomUUID();
        var entities = getRandomOrderItemEntityList();
        LocalDateTime releaseDateTime = LocalDateTime.now().plusDays(20);

        var entity = new OrderEntity(entities, releaseDateTime);
        entity.setUuid(uuid);

        OrderInfoResponseDTO responseDTO = orderMapper.toDTO(entity);

        assertEquals(entity.getUuid(), responseDTO.uuid());
        assertEquals(entity.getCreatedDateTime(), responseDTO.createDateTime());
        assertEquals(entity.getReleaseDateTime(), responseDTO.releaseDateTime());

        List<OrderItemDTO> mappedOrderItems = responseDTO.orderItems();

        assertEquals(entities.size(), mappedOrderItems.size());

        for (int i = 0; i < entities.size(); i++) {
            assertEquals(entities.get(i).getItemId(), mappedOrderItems.get(i).inventoryItemId());
            assertEquals(entities.get(i).getQuantity(), mappedOrderItems.get(i).quantity());
        }
    }

    private static Stream<Arguments> mappingOrderItemEntity_To_OrderItemDTOArgs() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(), 98),
                Arguments.of(UUID.randomUUID(), 65334),
                Arguments.of(UUID.randomUUID(), 1)
        );
    }

    private List<OrderItemEntity> getRandomOrderItemEntityList() {
        return List.of(
                new OrderItemEntity(UUID.randomUUID(), 21231),
                new OrderItemEntity(UUID.randomUUID(), 1),
                new OrderItemEntity(UUID.randomUUID(), 655)
        );
    }
}
