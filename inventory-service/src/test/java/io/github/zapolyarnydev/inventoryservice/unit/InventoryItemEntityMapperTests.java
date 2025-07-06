package io.github.zapolyarnydev.inventoryservice.unit;

import io.github.zapolyarnydev.inventoryservice.dto.ItemEntityDTO;
import io.github.zapolyarnydev.inventoryservice.dto.mapper.InventoryItemEntityMapper;
import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование маппинга сущностей предметов инвентаря")
public class InventoryItemEntityMapperTests {

    private final InventoryItemEntityMapper itemEntityMapper = Mappers.getMapper(InventoryItemEntityMapper.class);

    @ParameterizedTest(name = "{index} - Исходные uuid: {0}, имя: {1}, количество предметов {2}")
    @DisplayName("При маппинге сущности предмета инвентаря в DTO данные должны быть сохранены")
    @MethodSource("mappingEntityToDTOArgs")
    public void shouldMapInventoryItemEntity_ToDTO(UUID uuid, String name, int quantity) {
        var entity = new InventoryItemEntity(name, quantity);
        entity.setUuid(uuid);

        ItemEntityDTO dto = itemEntityMapper.toDTO(entity);

        assertEquals(entity.getUuid(), dto.uuid());
        assertEquals(entity.getName(), dto.name());
        assertEquals(entity.getQuantity(), dto.quantity());
    }

    private static Stream<Arguments> mappingEntityToDTOArgs() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(), "Bread", 75),
                Arguments.of(UUID.randomUUID(), "Apple", 6216),
                Arguments.of(UUID.randomUUID(), "Banana", 1)
        );
    }
}
