package io.github.zapolyarnydev.dto.mapper;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.dto.OrderInfoResponseDTO;
import io.github.zapolyarnydev.entity.OrderEntity;
import io.github.zapolyarnydev.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mappings({
            @Mapping(source = "uuid", target = "uuid"),
            @Mapping(source = "orderItems", target = "orderItems"),
            @Mapping(source = "createdDateTime", target = "createDateTime"),
            @Mapping(source = "releaseDateTime", target = "releaseDateTime")
    })
    OrderInfoResponseDTO toDTO(OrderEntity order);

    @Mappings({
            @Mapping(source = "itemId", target = "inventoryItemId"),
            @Mapping(source = "quantity", target = "quantity")
    })
    OrderItemDTO toDTO(OrderItemEntity orderItemEntity);

    List<OrderItemDTO> toItemDtoList(List<OrderItemEntity> orderItems);

}
