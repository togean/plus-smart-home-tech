package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignclient.PaymentClient;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    private PaymentService paymentService;

    @Override
    public PaymentDto addNewPayment(OrderDto orderDto) {
        return paymentService.addNewPayment(orderDto);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public void paymentResultSuccess(UUID orderId) {
        paymentService.paymentSuccess(orderId);
    }

    @Override
    public BigDecimal getPaymentProductCost(OrderDto orderDto) {
        return paymentService.getPaymentProductCost(orderDto);
    }

    @Override
    public void paymentResultFailed(UUID orderId) {
        paymentService.paymentFailed(orderId);
    }
}
