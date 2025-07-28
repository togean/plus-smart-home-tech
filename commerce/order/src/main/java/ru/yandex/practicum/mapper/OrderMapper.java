package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "status", target = "orderState")
    OrderDto orderToOrderDto(Order order);
}
