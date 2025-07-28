package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feignclient.DeliveryClient;
import ru.yandex.practicum.feignclient.PaymentClient;
import ru.yandex.practicum.feignclient.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getOrder(String username) {
        if (username.isEmpty() || username == null) {
            throw new NotAuthorizedUserException("OrderService: имя пользователя при запросе его заказов не указано");
        }
        log.info("OrderService: Запрос заказов от пользователя {}", username);
        return orderRepository.findByUsername(username).stream()
                .map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public OrderDto addOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("OrderService: Запрос на создани нового заказа {}", createNewOrderRequest);
        Order order = new Order();
        order.setShoppingCartId(createNewOrderRequest.getShoppingCart().getShoppingCartId());
        order.setProducts(createNewOrderRequest.getShoppingCart().getProducts());
        order.setStatus(OrderStatus.NEW);
        Order createdNewOrder = orderRepository.save(order);
        BookedProductsDto bookedProducts = warehouseClient.checkProductQuantity(createNewOrderRequest.getShoppingCart());
        createdNewOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        createdNewOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        createdNewOrder.setFragile(bookedProducts.getFragile());

        OrderDto orderDto = orderMapper.orderToOrderDto(createdNewOrder);
        BigDecimal productPrice = paymentClient.getPaymentProductCost(orderDto);
        BigDecimal totalPrice = paymentClient.getTotalCost(orderDto);

        createdNewOrder.setProductPrice(productPrice);
        createdNewOrder.setTotalPrice(totalPrice);

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setOrderId(createdNewOrder.getOrderId());
        deliveryDto.setToAddress(createNewOrderRequest.getDeliveryAddress());
        deliveryDto.setFromAddress(warehouseClient.getAddress());
        createdNewOrder.setDeliveryId(deliveryClient.addNewDelivery(deliveryDto).getDeliveryId());

        paymentClient.addNewPayment(orderMapper.orderToOrderDto(createdNewOrder));
        return orderMapper.orderToOrderDto(createdNewOrder);
    }

    @Override
    public OrderDto returnProduct(ProductReturnRequest productReturnRequest) {
        log.info("OrderService: Запрос на возврат товаров {}", productReturnRequest);
        Order order = orderRepository.findById(productReturnRequest.getOrderId()).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        warehouseClient.returnToWarehouse(productReturnRequest.getProducts());
        order.setStatus(OrderStatus.PRODUCT_RETURNED);
        orderRepository.save(order);
        log.info("OrderService: Запрос на возврат товаров выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("OrderService: Запрос на оплату заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("OrderService: Запрос на оплату заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderService: Запрос эмуляции ошибки оплаты заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);
        log.info("OrderService: Запрос эмуляции ошибки оплаты заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("OrderService: Запрос доставки заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        log.info("OrderService: Запрос доставки заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderService: Запрос эмуляции ошибки доставки заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.DELIVERY_FAILED);
        orderRepository.save(order);
        log.info("OrderService: Запрос эмуляции ошибки доставки заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto completed(UUID orderId) {
        log.info("OrderService: Запрос на завершение заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
        log.info("OrderService: Запрос на завершение заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("OrderService: Запрос на расчёт итоговой стоимости заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setTotalPrice(paymentClient.getTotalCost(orderMapper.orderToOrderDto(order)));
        orderRepository.save(order);
        log.info("OrderService: Запрос на расчёт итоговой стоимости заказа c Id {} выполнен", orderId);
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("OrderService: Запрос на расчёт доставки заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.orderToOrderDto(order));
        order.setTotalPrice(deliveryPrice);
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("OrderService: Запрос на сборку заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.ASSEMBLED);
        orderRepository.save(order);
        log.info("OrderService: Запрос на сборку заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderService: Запрос на эмуляцию ошибки при сборке заказа c Id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(OrderStatus.ASSEMBLY_FAILED);
        orderRepository.save(order);
        log.info("OrderService: Запрос на эмуляцию ошибки при сборке заказа выполнен");
        return orderMapper.orderToOrderDto(order);
    }
}
