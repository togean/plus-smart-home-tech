package ru.yandex.practicum.service;

import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto addNewDelivery(DeliveryDto deliveryDto);

    void emulateSuccessfulDelivery(UUID orderId);

    void pickedDelivery(UUID orderId);

    void emulateFailedDelivery(UUID orderId);

    BigDecimal deliveryCost(OrderDto orderDto);
}
