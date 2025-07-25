package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ShippedToDeliveryRequest {
    @NotNull
    UUID orderId;
    @NotNull
    UUID deliveryId;
}