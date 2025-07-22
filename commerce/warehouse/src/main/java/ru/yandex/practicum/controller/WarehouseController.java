package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.WarehouseClient;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("WarehouseController: Запрос на добавление нового товара {}", request);
        warehouseService.addNewProduct(request);
    }

    @Override
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCartDto) {
        log.info("WarehouseController: Запрос на проверку кол-ва товара {}", shoppingCartDto);
        return warehouseService.checkProductQuantity(shoppingCartDto);
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        log.info("WarehouseController: Запрос на добавление товара {}", request);
        warehouseService.addProductQuantity(request);
        log.info("WarehouseController: Товар успешно добавлен");
    }

    @Override
    public AddressDto getAddress() {
        log.info("WarehouseController: Запрос адреса склада");
        return warehouseService.getAddress();
    }
}