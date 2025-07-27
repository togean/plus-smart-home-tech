package ru.yandex.practicum.service;

import ru.yandex.practicum.model.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    void addProductQuantity(AddProductToWarehouseRequest requestDto);

    BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCartDto);

    AddressDto getAddress();

    void shippedDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest);

    void returnToWarehouse(Map<UUID, Long> products);

    BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest);
}