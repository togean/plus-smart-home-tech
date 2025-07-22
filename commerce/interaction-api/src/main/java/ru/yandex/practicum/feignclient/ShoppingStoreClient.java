package ru.yandex.practicum.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.*;

import java.util.UUID;

@FeignClient(name="shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PutMapping
    ProductDto addProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    void removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest);

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam String category,
                                 @RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(defaultValue = "productName") String sort);
}
