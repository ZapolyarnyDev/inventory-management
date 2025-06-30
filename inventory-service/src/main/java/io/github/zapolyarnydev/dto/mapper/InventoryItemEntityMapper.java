package io.github.zapolyarnydev.dto.mapper;

import io.github.zapolyarnydev.dto.ItemEntityDTO;
import io.github.zapolyarnydev.entity.InventoryItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryItemEntityMapper {

    ItemEntityDTO toDTO(InventoryItemEntity itemEntity);
}
