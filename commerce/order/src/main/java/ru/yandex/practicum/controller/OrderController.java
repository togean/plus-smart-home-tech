package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.OrderClient;
import ru.yandex.practicum.model.CreateNewOrderRequest;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private OrderService orderService;

    @Override
    public List<OrderDto> getOrder(String username) {
        log.info("OrderController: Запрос на получение заказов пользователя {}", username);
        return orderService.getOrder(username);
    }

    @Override
    public OrderDto addOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("OrderController: Запрос на создани нового заказа {}", createNewOrderRequest);
        return orderService.addOrder(createNewOrderRequest);
    }

    @Override
    public OrderDto returnProduct(ProductReturnRequest productReturnRequest) {
        log.info("OrderController: Запрос на возврат заказа {}", productReturnRequest);
        return orderService.returnProduct(productReturnRequest);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("OrderController: Запрос на оплату заказа {}", orderId);
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderController: Запрос на эмуляцию ошибки при оплате заказа {}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("OrderController: Запрос на доставку заказа {}", orderId);
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderController: Запрос на эмуляцию ошибки при доставке заказа {}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto completed(UUID orderId) {
        log.info("OrderController: Запрос на выполнение завершения заказа {}", orderId);
        return orderService.completed(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("OrderController: Запрос на расчёт стоимости заказа {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("OrderController: Запрос на расчёт стоимости доставки заказа {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("OrderController: Запрос сборку заказа {}", orderId);
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderController: Запрос на эмуляцию ошибки при сборке заказа {}", orderId);
        return orderService.assemblyFailed(orderId);
    }
}
