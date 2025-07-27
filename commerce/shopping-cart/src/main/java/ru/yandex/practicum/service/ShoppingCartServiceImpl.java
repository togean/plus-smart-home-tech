package ru.yandex.practicum.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NoProductsShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feignclient.WarehouseClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("ShoppingCartService: Получение данных по корзине товаров для пользователя {}", username);
        validateUser(username);
        Optional<ShoppingCart> cart = shoppingCartRepository.findByUsernameAndActive(username, true);

        if (cart.isPresent()) {
            return shoppingCartMapper.shoppingCartDtoToCart(cart.get());
        }
        log.info("ShoppingCartService: У пользователя нет корзины товаров, создаю для него новую корзину");
        ShoppingCart newUserCart = new ShoppingCart();
        newUserCart.setUsername(username);
        newUserCart.setActive(true);
        newUserCart.setCartProducts(new HashMap<>());

        cart = Optional.of(shoppingCartRepository.save(newUserCart));

        return shoppingCartMapper.shoppingCartDtoToCart(cart.get());
    }

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        log.info("ShoppingCartService: Добавление новой корзины товаров для пользователя {}", username);
        validateUser(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> shoppingCartRepository.save(ShoppingCart.builder()
                        .username(username)
                        .active(true)
                        .cartProducts(new HashMap<>())
                        .build()));

        try {
            warehouseClient.checkProductQuantity(shoppingCartMapper.shoppingCartDtoToCart(cart));
        } catch (FeignException e) {
            log.error("ShoppingCartService: Модуль склада пока недоступен: {}", e.getMessage());
            throw new IllegalStateException("ShoppingCartService: Модуль склада пока недоступен. Попробуйте выполнить обращение позже");
        }

        cart.setCartProducts(products);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.shoppingCartDtoToCart(savedShoppingCart);
    }

    @Override
    public void deactivateCart(String username) {
        log.info("ShoppingCartService: Деактивация корзины пользователя: {}", username);
        validateUser(username);
        ShoppingCart userCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new NotFoundException("ShoppingCartService: Не найдена корзина для пользователя "+username));
        userCart.setActive(false);
        shoppingCartRepository.save(userCart);
    }

    @Override
    public ShoppingCartDto deleteProduct(String username, List<UUID> cartProducts) {
        log.info("ShoppingCartService: Удаление товаров из корзины пользователя");
        validateUser(username);
        ShoppingCart userCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new NotFoundException("ShoppingCartService: Не найдена корзина для пользователя"));
        log.info("ShoppingCartService: Товары для удаления: {}", userCart);
        if (!userCart.getCartProducts().keySet().containsAll(cartProducts)) {
            throw new NoProductsShoppingCartException("ShoppingCartService: Удаляемый товар в корзине не найден");
        }
        cartProducts.forEach(userCart.getCartProducts()::remove);
        userCart = shoppingCartRepository.save(userCart);
        log.info("ShoppingCartService: Удаление товаров из корзины произошло успешно");
        return shoppingCartMapper.shoppingCartDtoToCart(userCart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest) {
        log.info("ShoppingCartService: Изменение кол-ва товаров в корзине пользователя");
        validateUser(username);
        ShoppingCart userCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new NotFoundException("ShoppingCartService: Не найдена корзина для пользователя"));

        userCart.getCartProducts().put(changeProductQuantityRequest.getProductId(), changeProductQuantityRequest.getNewQuantity());
        warehouseClient.checkProductQuantity(shoppingCartMapper.shoppingCartDtoToCart(userCart));
        userCart = shoppingCartRepository.save(userCart);
        log.info("ShoppingCartService: Изменение кол-ва товара успешно завершено");
        return shoppingCartMapper.shoppingCartDtoToCart(userCart);
    }

    @Override
    public BookedProductsDto bookShoppingCartInWarehouse(String username) {
        log.info("ShoppingCartService: Запрос на бронирование корзины товаров для пользователя {}", username);
        validateUser(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("ShoppingCartService: не найдена корзина товаров для пользователя" + username));
        if (cart.getCartProducts() == null || cart.getCartProducts().isEmpty()) {
            throw new ValidationException("ShoppingCartService: в корзине пользователя "+username+" нет товаров.");
        }
        log.info("ShoppingCartService: Запрос на бронирование корзины товаров для пользователя {} выполнен", username);
        return warehouseClient.checkProductQuantity(shoppingCartMapper.shoppingCartDtoToCart(cart));
    }

    @Override
    public String getUserName(UUID cartId) {
        log.info("ShoppingCartService: Запрос на получения пользователя по корзине товаров");
        ShoppingCart cartUser = shoppingCartRepository.findByCartId(cartId)
                .orElseThrow(() -> new NotFoundException("Пользователя с картой " + cartId + " нет"));
        return cartUser.getUsername();
    }

    private void validateUser(String username) {
        log.info("ShoppingCartService: Проверка имени пользователя: {}", username);
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("ShoppingCartService: Не указано имя пользователя. Имя не должно быть пустым.");
        }
        log.info("ShoppingCartService: Проверка пользователя успешно заверщена");
    }
}
