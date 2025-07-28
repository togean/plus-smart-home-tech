package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouse_products")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @AttributeOverrides({
            @AttributeOverride(name = "width", column = @Column(name = "width")),
            @AttributeOverride(name = "height", column = @Column(name = "height")),
            @AttributeOverride(name = "depth", column = @Column(name = "depth"))
    })
    Dimension dimension;
    private double weight;
    private boolean fragile;
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Long quantity = 0L;
}