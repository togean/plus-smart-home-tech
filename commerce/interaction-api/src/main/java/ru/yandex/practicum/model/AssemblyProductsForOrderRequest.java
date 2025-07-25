package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotNull
    UUID shoppingCartId;
    @NotNull
    UUID orderId;
}
