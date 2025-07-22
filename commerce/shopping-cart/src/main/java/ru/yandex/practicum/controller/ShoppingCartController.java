package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.ShoppingCartClient;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@AllArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;


    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        log.info("ShoppingCartController: Поступил запрос на добавление товара");
        return shoppingCartService.addProduct(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest) {
        log.info("ShoppingCartController: Поступил запрос на изменение кол-ва товара");
        return shoppingCartService.changeProductQuantity(username, changeProductQuantityRequest);

    }

    @Override
    public ShoppingCartDto deleteProduct(String username, List<UUID> products) {
        log.info("ShoppingCartController: Поступил запрос на удаление товара");
        return shoppingCartService.deleteProduct(username, products);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("ShoppingCartController: Поступил запрос на получение корзины товаров");
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public void deactivateCart(String username) {
        log.info("ShoppingCartController: Поступил запрос на декативацию товара");
        shoppingCartService.deactivateCart(username);
    }
}
