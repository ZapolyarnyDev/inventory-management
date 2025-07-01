package io.github.zapolyarnydev.dto.mapper;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.dto.OrderItemEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @Mappings({
            @Mapping(source = "inventoryItemId", target = "inventoryItemId"),
            @Mapping(source = "quantity", target = "quantity")
    })
    OrderItemEventDTO toEventDto(OrderItemDTO orderItemDTO);

    List<OrderItemEventDTO> toEventDto(List<OrderItemDTO> orderItems);

    @Mappings({
            @Mapping(source = "inventoryItemId", target = "inventoryItemId"),
            @Mapping(source = "quantity", target = "quantity")
    })
    OrderItemDTO toItemDto(OrderItemEventDTO orderItemEventDTO);

    List<OrderItemDTO> toItemDto(List<OrderItemEventDTO> orderItems);
}
