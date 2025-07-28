package ru.yandex.practicum.exception;

public class DeliveryAlreadyExistException extends RuntimeException{
    public DeliveryAlreadyExistException(String message) {
        super(message);
    }
}
