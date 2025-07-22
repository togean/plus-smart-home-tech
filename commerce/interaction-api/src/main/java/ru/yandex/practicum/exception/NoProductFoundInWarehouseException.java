package ru.yandex.practicum.exception;

public class NoProductFoundInWarehouseException extends RuntimeException {
    public NoProductFoundInWarehouseException(String message) {
        super(message);
    }
}