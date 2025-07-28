package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feignclient.OrderClient;
import ru.yandex.practicum.feignclient.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;
    private PaymentMapper paymentMapper;
    private OrderClient orderClient;
    private ShoppingStoreClient shoppingStoreClient;

    @Override
    public PaymentDto addNewPayment(OrderDto orderDto) {
        log.info("PaymentService: Создание новой оплаты по заказу {}", orderDto);

        Payment payment = new Payment();
        payment.setPaymentId(orderDto.getOrderId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTotalPayment(orderDto.getTotalPrice());
        payment.setDeliveryTotal(orderDto.getDeliveryPrice());
        payment.setFeeTotal(orderDto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)));
        paymentRepository.save(payment);
        log.info("PaymentService: Создание новой оплаты по заказу выполнено");
        return paymentMapper.paymentToPaymentDto(payment);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        log.info("PaymentService: Расчёт полной оплаты по заказу {}", orderDto);

        BigDecimal totalCost = orderDto.getProductPrice()
                .multiply(BigDecimal.valueOf(0.1))
                .add(orderDto.getDeliveryPrice());
        log.info("PaymentService: Расчёт полной оплаты выполнен");
        return totalCost;
    }

    @Override
    public void paymentSuccess(UUID orderId) {
        log.info("PaymentService: Запрос эмуляции успешгого прохождения платежа {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("PaymentService: Заказ с данным Id не найден"));
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        orderClient.payment(payment.getOrderId());
        log.info("PaymentService: Запрос эмуляции успешгого прохождения платежа выполнен");
    }

    @Override
    public BigDecimal getPaymentProductCost(OrderDto orderDto) {
        log.info("PaymentService: Запрос расчёта стоимости товаров заказа {}", orderDto);
        BigDecimal paymentProductCost = BigDecimal.ZERO;
        Map<UUID, Long> productsInOrder = orderDto.getProducts();

        for (Map.Entry<UUID, Long> entry : productsInOrder.entrySet()) {
            ProductDto product = shoppingStoreClient.getProduct(entry.getKey());
            paymentProductCost = paymentProductCost.add(BigDecimal.valueOf(product.getPrice() * entry.getValue()));
        }
        log.info("PaymentService: Запрос расчёта стоимости товаров заказа выполнен");
        return paymentProductCost;
    }

    @Override
    public void paymentFailed(UUID orderId) {
        log.info("PaymentService: Запрос эмуляции ошибки прохождения платежа {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("PaymentService: Заказ с данным Id не найден"));
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        orderClient.payment(payment.getOrderId());
        log.info("PaymentService: Запрос эмуляции ошибки прохождения платежа выполнен");

    }
}