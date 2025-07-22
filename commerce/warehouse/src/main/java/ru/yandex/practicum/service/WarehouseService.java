package ru.yandex.practicum.service;

import ru.yandex.practicum.model.*;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    void addProductQuantity(AddProductToWarehouseRequest requestDto);

    BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCartDto);

    AddressDto getAddress();
}