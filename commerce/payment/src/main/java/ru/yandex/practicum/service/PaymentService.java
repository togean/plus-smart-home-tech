package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto addNewPayment(OrderDto orderDto);
    BigDecimal getTotalCost(OrderDto orderDto);
    void paymentSuccess(UUID orderId);
    BigDecimal getPaymentProductCost(OrderDto orderDto);
    void paymentFailed(UUID orderId);
}
