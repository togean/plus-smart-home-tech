package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.feignclient.OrderClient;
import ru.yandex.practicum.feignclient.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;
    private PaymentMapper paymentMapper;
    private OrderClient orderClient;
    private ShoppingStoreClient shoppingStoreClient;

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
    public BigDecimal getPaymentProductCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public void paymentFailed(UUID orderId) {

    }
}
