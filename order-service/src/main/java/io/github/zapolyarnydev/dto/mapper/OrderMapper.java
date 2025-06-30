package io.github.zapolyarnydev.dto.mapper;

import io.github.zapolyarnydev.dto.OrderItemDTO;
import io.github.zapolyarnydev.dto.OrderInfoResponseDTO;
import io.github.zapolyarnydev.entity.OrderEntity;
import io.github.zapolyarnydev.entity.OrderItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderInfoResponseDTO toDTO(OrderEntity order);

    OrderItemDTO toDTO(OrderItemEntity orderItemEntity);

    List<OrderItemDTO> toItemDtoList(List<OrderItemEntity> orderItems);

}
