package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Page<ProductDto> getProductsByCategory(String productCategory, Pageable pageable) {
        log.info("ShoppingStoreService: Запрос товаров по категории {}", productCategory);
        return shoppingStoreRepository.findAllByProductCategory(ProductCategory.valueOf(productCategory), pageable)
                .map(ProductMapper.INSTANCE::productToProductDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("ShoppingStoreService: Запрос товара с UUID:{}", productId);
        Product product = shoppingStoreRepository.findByProductId(productId).orElseThrow(
                () -> new NotFoundException("Товар не найден"));
        return productMapper.productToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        log.info("ShoppingStoreService: Запрос на добавление нового товара:{}", productDto);
        Product newProduct = productMapper.productDtoToProduct(productDto);
        Product savedProduct = shoppingStoreRepository.save(newProduct);
        return productMapper.productToProductDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("ShoppingStoreService: Запрос на обновление товара с UUID:{}", productDto.getProductId());
        if (!shoppingStoreRepository.existsById(productDto.getProductId())) {
            throw new NotFoundException("ShoppingStoreService: Товар не найден");
        }
        return productMapper.productToProductDto(shoppingStoreRepository.save(productMapper.productDtoToProduct(productDto)));
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {
        log.info("ShoppingStoreService: Запрос на удаление товара с UUID:{}", productId);
        Product product = shoppingStoreRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("ShoppingStoreService: Товар не найден"));
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
        log.info("ShoppingStoreService: Товар с UUID:{} успешно деактивирован", productId);
    }

    @Transactional
    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("ShoppingStoreService: Изменение статуса у товара с id: {} и установка ему статуса: {}", setProductQuantityStateRequest.getProductId(), setProductQuantityStateRequest.getQuantityState());
        Product product = shoppingStoreRepository.findByProductId(setProductQuantityStateRequest.getProductId()).orElseThrow(() -> new NotFoundException("Товар не найден"));
        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        shoppingStoreRepository.save(product);
        log.info("ShoppingStoreService: Статус у товара успешно обновлён");
        return true;
    }
}
