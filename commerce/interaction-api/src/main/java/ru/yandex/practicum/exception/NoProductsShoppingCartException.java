package ru.yandex.practicum.exception;

public class NoProductsShoppingCartException extends RuntimeException {
    public NoProductsShoppingCartException(String message) {
        super(message);
    }
}
