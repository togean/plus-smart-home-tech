package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.PaymentClient;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.repository.PaymantRepository;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    private PaymantRepository paymantRepository;

    @Override
    public PaymentDto addNewPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public void paymentSuccess(UUID orderId) {

    }

    @Override
    public Double getPaymentProductCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public void paymentFailed(UUID orderId) {

    }
}
