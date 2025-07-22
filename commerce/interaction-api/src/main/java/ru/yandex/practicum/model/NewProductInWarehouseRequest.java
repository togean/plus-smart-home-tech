package ru.yandex.practicum.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private DimensionDto dimension;

    @Min(1)
    private Double weight;

    private Boolean fragile;
}
