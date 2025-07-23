package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
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
