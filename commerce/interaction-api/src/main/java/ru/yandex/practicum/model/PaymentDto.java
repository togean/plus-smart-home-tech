package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    @NotNull
    private UUID paymentId;

    @NotNull
    private Double totalPayment;

    @NotNull
    private Double deliveryTotal;

    @NotNull
    private Double feeTotal;
}
