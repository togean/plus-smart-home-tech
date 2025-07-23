package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.OrderClient;
import ru.yandex.practicum.model.CreateNewOrderRequest;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.ProductReturnRequest;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private OrderRepository orderRepository;

    @Override
    public List<OrderDto> getOrder(String username) {
        return null;
    }

    @Override
    public OrderDto addOrder(CreateNewOrderRequest createNewOrderRequest) {
        return null;
    }

    @Override
    public OrderDto returnProduct(ProductReturnRequest productReturnRequest) {
        return null;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto completed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return null;
    }
}
