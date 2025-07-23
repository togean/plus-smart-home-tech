package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.ShoppingStoreClient;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("ShoppingStoreController: Запрос на получение товара с UUID {}", productId);
        return shoppingStoreService.getProduct(productId);
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: Запрос на добавление товара: {}", productDto);
        return shoppingStoreService.addProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: Запрос на обновление данных по товару: {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public void removeProduct(UUID productId) {
        log.info("ShoppingStoreController: Запрос на удаление товара c UUID {}", productId);
        shoppingStoreService.deleteProduct(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("ShoppingStoreController: Установка статуса кол-ва товара");
        return shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest);
    }

    @Override
    public Page<ProductDto> getProducts(@RequestParam String category,
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(defaultValue = "productName") String sort) {
        log.info("ShoppingStoreController: Получение товаров по категории");
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort);
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }
}
