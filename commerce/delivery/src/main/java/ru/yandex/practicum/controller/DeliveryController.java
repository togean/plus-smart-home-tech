package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.DeliveryClient;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private DeliveryService deliveryService;

    @Override
    public DeliveryDto addNewDelivery(DeliveryDto deliveryDto) {
        log.info("DeliveryController: Запрос на создание новой доставки {}",deliveryDto);
        return deliveryService.addNewDelivery(deliveryDto);
    }

    @Override
    public void emulateSuccessfulDelivery(UUID orderId) {
        log.info("DeliveryController: Эмуляция успешной доставки с id {}",orderId);
        deliveryService.emulateSuccessfulDelivery(orderId);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        log.info("DeliveryController: Добавлене товара в достаку с id {}",orderId);
        deliveryService.pickedDelivery(orderId);
    }

    @Override
    public void emulateFailedDelivery(UUID orderId) {
        log.info("DeliveryController: Эмуляция неуспешной доставки с id {}",orderId);
        deliveryService.emulateFailedDelivery(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        log.info("DeliveryController: Расчёт стоимости доставки {}",orderDto);
        return deliveryService.deliveryCost(orderDto);
    }
}
