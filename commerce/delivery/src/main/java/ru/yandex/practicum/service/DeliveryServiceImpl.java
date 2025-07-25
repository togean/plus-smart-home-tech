package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.DeliveryAlreadyExistException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feignclient.OrderClient;
import ru.yandex.practicum.feignclient.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private static final BigDecimal BASE_DELIVERY_PRICE = BigDecimal.valueOf(5.0);
    private static final BigDecimal WAREHOUSE1_PRICE = BigDecimal.valueOf(1);
    private static final BigDecimal WAREHOUSE2_PRICE = BigDecimal.valueOf(2);
    private static final BigDecimal FRAGILE_PRICE = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_PRICE = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_PRICE = BigDecimal.valueOf(0.2);
    private static final BigDecimal DIFFERENT_STREET_PRICE = BigDecimal.valueOf(0.2);
    private DeliveryRepository deliveryRepository;
    private DeliveryMapper deliveryMapper;
    private OrderClient orderClient;
    private WarehouseClient warehouseClient;

    @Override
    public DeliveryDto addNewDelivery(DeliveryDto deliveryDto) {
        log.info("DeliveryService: Создание новой доставки для заказа {}", deliveryDto);
        if(deliveryRepository.existsById(deliveryDto.getDeliveryId())){
            throw new DeliveryAlreadyExistException("Данная доставка уже запланирована");
        }
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery.setDeliveryStatus(DeliveryStatus.CREATED);
        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void emulateSuccessfulDelivery(UUID deliveryId) {
        log.info("DeliveryService: Эмуляция успешной доставки заказа с Id {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NotFoundException("Доставка c указанным Id не найдена"));
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        orderClient.completed(delivery.getOrderId());
        log.info("DeliveryService: Доставка успешно выполнена");
    }

    @Override
    public void pickedDelivery(UUID deliveryId) {
        log.info("DeliveryService: Эмуляция получения товара для доставки (Id {})", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NotFoundException("Доставка c указанным Id не найдена"));
        delivery.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
        deliveryRepository.save(delivery);
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedDelivery(deliveryRequest);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        log.info("DeliveryService: Эмуляция неудачной доставки (Id {})", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NotFoundException("Доставка c указанным Id не найдена"));
        delivery.setDeliveryStatus(DeliveryStatus.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        log.info("DeliveryService: Расчёт стоимости доставки {})", orderDto);
        Delivery delivery = deliveryRepository.findById(orderDto.getOrderId()).orElseThrow(
                () -> new NotFoundException("Доставка c указанным Id не найдена"));
        Address warehouseAddress = delivery.getFromAddress();
        Address customerAddress = delivery.getToAddress();
        BigDecimal deliveryCost = BASE_DELIVERY_PRICE;
        if (orderDto.getFragile()) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(FRAGILE_PRICE));
        }
        if (warehouseAddress.getCity().equals("ADDRESS_1")) {
            deliveryCost = deliveryCost.multiply(WAREHOUSE1_PRICE);
        } else {
            deliveryCost = deliveryCost.multiply(WAREHOUSE2_PRICE);
        }
        if (!warehouseAddress.getStreet().equals(customerAddress.getStreet())) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(DIFFERENT_STREET_PRICE));
        }
        if (orderDto.getDeliveryWeight() != null) {
            deliveryCost = deliveryCost.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(WEIGHT_PRICE));
        }

        if (orderDto.getDeliveryVolume() != null) {
            deliveryCost = deliveryCost.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(VOLUME_PRICE));
        }

        return deliveryCost;
    }
}
