package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NonNull
    private QuantityState quantityState;
}
