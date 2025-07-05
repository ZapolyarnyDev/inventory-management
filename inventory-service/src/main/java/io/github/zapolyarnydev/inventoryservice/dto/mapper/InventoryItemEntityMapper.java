package io.github.zapolyarnydev.inventoryservice.dto.mapper;

import io.github.zapolyarnydev.inventoryservice.dto.ItemEntityDTO;
import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InventoryItemEntityMapper {

    @Mappings({
            @Mapping(source = "uuid", target = "uuid"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "quantity", target = "quantity")
    })
    ItemEntityDTO toDTO(InventoryItemEntity itemEntity);
}
