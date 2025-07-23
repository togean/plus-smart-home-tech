package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "username")
    private String username;

    private boolean active;

    @ElementCollection
    @Builder.Default
    @CollectionTable(name = "cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    private Map<UUID, @Positive Long> cartProducts = new HashMap<>();
}