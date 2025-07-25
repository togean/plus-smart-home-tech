package ru.yandex.practicum.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PutMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProduct(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getAddress();
    @PostMapping("/shipped")
    void shippedDelivery(@RequestBody ShippedToDeliveryRequest shippedToDeliveryRequest);

    @PostMapping("/return")
    void returnToWarehouse(@RequestBody Map<UUID,Long> products);

    @PostMapping("/assembly")
    BookedProductsDto asemblyOrder(@RequestBody AssemblyProductsForOrderRequest assemblyProductsForOrderRequest);
}
