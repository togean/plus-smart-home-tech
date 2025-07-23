package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.DeliveryClient;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private DeliveryService deliveryService;

    @Override
    public DeliveryDto addNewDelivery(DeliveryDto deliveryDto) {
        return null;
    }

    @Override
    public void emulateSuccessfulDelivery(UUID orderId) {

    }

    @Override
    public void pickedDelivery(UUID orderId) {

    }

    @Override
    public void failedDelivery(UUID orderId) {

    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        return null;
    }
}
