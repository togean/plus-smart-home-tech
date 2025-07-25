package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryDto {
    @NotNull
    UUID deliveryId;
    @NotNull
    AddressDto fromAddress;
    @NotNull
    AddressDto toAddress;
    @NotNull
    UUID orderId;
    @NotNull
    DeliveryStatus deliveryStatus;
}
