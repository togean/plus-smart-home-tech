package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartDto;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(source = "cartId", target = "shoppingCartId")
    @Mapping(source = "cartProducts", target = "products")
    ShoppingCartDto shoppingCartDtoToCart(ShoppingCart cart);
}
