package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID paymentId;

    @NotNull
    private UUID deliveryId;

    @NotNull
    private UUID shoppingCartId;

    private OrderStatus orderState;

    private Boolean fragile;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private BigDecimal totalPrice;

    private BigDecimal deliveryPrice;

    private BigDecimal productPrice;

    @NotNull
    private Map<UUID, Long> products;
}
