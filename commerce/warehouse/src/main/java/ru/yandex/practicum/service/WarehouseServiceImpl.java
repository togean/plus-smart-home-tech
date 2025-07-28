package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NoProductFoundInWarehouseException;
import ru.yandex.practicum.exception.NotEnoughProductsInWarehouse;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ProductAlreadyExistException;
import ru.yandex.practicum.mapper.BookingMapper;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("WarehouseService: Добавление новго товара на склад:{}", newProductInWarehouseRequest);
        if (warehouseRepository.existsById(newProductInWarehouseRequest.getProductId())) {
            throw new ProductAlreadyExistException("Такой товар уже есть на складе");
        }
        warehouseRepository.save(warehouseMapper.toWarehouse(newProductInWarehouseRequest));
        log.info("WarehouseService: Товар успешно добавлен");
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest requestDto) {
        log.info("WarehouseService: Добавление данных о количестве товара {}", requestDto);

        WarehouseProduct product = warehouseRepository.findById(requestDto.getProductId()).orElseThrow(
                () -> new NoProductFoundInWarehouseException("WarehouseService: Товар и информация о товаре не найдены.")
        );
        Long currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0L;
        product.setQuantity(currentQuantity + requestDto.getQuantity());
        warehouseRepository.save(product);
        log.info("WarehouseService: Данные о товаре успешно добавлены");
    }

    @Override
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCartDto) {
        log.info("WarehouseService: Проверка кол-ва товара для заказа {}", shoppingCartDto);
        double weightOfProductsInCard = 0;
        double volumeOfProductsInCart = 0;
        boolean isAnyFragile = false;

        Set<UUID> productsInCart = shoppingCartDto.getProducts().keySet();
        List<WarehouseProduct> productsExistedInWarehouse = warehouseRepository.findAllById(productsInCart);
        Set<UUID> idsOfExistedInWarehouseProducts = productsExistedInWarehouse.stream()
                .map(WarehouseProduct::getProductId)
                .collect(Collectors.toSet());
        Set<UUID> idsOfMissedInWarehouseProducts = new HashSet<>(productsInCart);
        idsOfMissedInWarehouseProducts.removeAll(idsOfExistedInWarehouseProducts);
        if (!idsOfMissedInWarehouseProducts.isEmpty()) {
            throw new NotFoundException("WarehouseService: Данный список товаров отсутствует на складе: " + idsOfMissedInWarehouseProducts);
        }

        for (WarehouseProduct product : productsExistedInWarehouse) {
            if (product.getQuantity() < shoppingCartDto.getProducts().get(product.getProductId())) {
                throw new NotEnoughProductsInWarehouse("WarehouseService: Товара с id:" + product.getProductId() + " недостаточно на складе");
            }
        }

        log.info("WarehouseService: Все заказанные товары есть на складе в нужном кол-ве");

        for (WarehouseProduct product : productsExistedInWarehouse) {
            weightOfProductsInCard += product.getWeight() * product.getQuantity();
            volumeOfProductsInCart += product.getDimension().getDepth() * product.getDimension().getWidth() * product.getDimension().getHeight() * product.getQuantity();
            if (product.isFragile()) {
                isAnyFragile = true;
            }
        }
        log.info("WarehouseService: Расчёт характеристик товаров выполнен успешно");
        log.info("WarehouseService: Проверка кол-ва товаров выполнена успешно");
        log.info("WarehouseService: weightOfProductsInCard = {}", weightOfProductsInCard);
        log.info("WarehouseService: volumeOfProductsInCart = {}", volumeOfProductsInCart);
        log.info("WarehouseService: isAnyFragile = {}", isAnyFragile);
        return BookedProductsDto.builder()
                .deliveryWeight(weightOfProductsInCard)
                .deliveryVolume(volumeOfProductsInCart)
                .fragile(isAnyFragile)
                .build();
    }

    @Override
    public AddressDto getAddress() {
        log.info("WarehouseService: Запрос адреса склада");
        String address = Address.CURRENT_ADDRESS;
        AddressDto addressDto = AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
        return addressDto;
    }

    @Override
    public void shippedDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
        log.info("WarehouseService: Запрос доставку заказа {}", shippedToDeliveryRequest.getOrderId());
        Optional<Booking> booking = bookingRepository.findByOrderId(shippedToDeliveryRequest.getOrderId());
        if (booking.isEmpty()) {
            throw new NotFoundException("WarehouseService: Заказ с таким Id:" + shippedToDeliveryRequest.getOrderId() + " не найден");
        }
        booking.get().setDeliveryId(shippedToDeliveryRequest.getDeliveryId());
        bookingRepository.save(booking.get());
    }

    @Override
    public void returnToWarehouse(Map<UUID, Long> products) {
        log.info("WarehouseService: Запрос на возврат товара");
        List<WarehouseProduct> existedProductsInWarehouse = warehouseRepository.findAllById(products.keySet());
        Set<WarehouseProduct> productsToBeUpdated = new HashSet<>();
        for (WarehouseProduct product : existedProductsInWarehouse) {
            Long productsQuantity = products.get(product.getProductId());
            product.setQuantity(product.getQuantity() + productsQuantity);
            productsToBeUpdated.add(product);
        }
        warehouseRepository.saveAll(productsToBeUpdated);
    }

    @Override
    public BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest) {
        log.info("WarehouseService: Запрос сборки товаров для заказа с id {}", assemblyProductsForOrderRequest.getShoppingCartId());
        UUID shoppingCartId = assemblyProductsForOrderRequest.getShoppingCartId();
        Optional<Booking> booking = bookingRepository.findByOrderId(shoppingCartId);
        if (booking.isEmpty()) {
            throw new NotFoundException("WarehouseService: Заказ с таким id не найден");
        }
        Map<UUID, Long> productsInBooking = booking.get().getProducts();
        List<WarehouseProduct> existedProductsInWarehouse = warehouseRepository.findAllById(productsInBooking.keySet());

        existedProductsInWarehouse.forEach(product -> {
            if (product.getQuantity() < productsInBooking.get(product.getProductId())) {
                throw new NotEnoughProductsInWarehouse("WarehouseService: на складе нет необходимого кол-ва товара с Id:" + product.getProductId());
            }
        });
        for (WarehouseProduct product : existedProductsInWarehouse) {
            product.setQuantity(product.getQuantity() - productsInBooking.get(product.getProductId()));
        }
        booking.get().setOrderId(assemblyProductsForOrderRequest.getOrderId());
        log.info("WarehouseService: Запрос сборку товаров для заказа с id {} завершён", assemblyProductsForOrderRequest.getShoppingCartId());
        return bookingMapper.toBookedProductsDto(booking.get());
    }
}
