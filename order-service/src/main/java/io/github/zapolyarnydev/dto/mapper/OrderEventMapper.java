package io.github.zapolyarnydev.dto.mapper;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.dto.OrderItemEventDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    OrderItemEventDTO toEventDto(OrderItemDTO orderItemDTO);

    List<OrderItemEventDTO> toEventDto(List<OrderItemDTO> orderItems);

    OrderItemDTO toItemDto(OrderItemEventDTO orderItemEventDTO);

    List<OrderItemDTO> toItemDto(List<OrderItemEventDTO> orderItems);
}
