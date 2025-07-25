package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@Table(name = "delivery")
public class Delivery {
    @Id
    @UuidGenerator
    @Column(name="delivery_id")
    private UUID deliveryId;

    @Column(name="order_id")
    private UUID orderId;

    @Enumerated(value = EnumType.STRING)
    @Column(name="status")
    private DeliveryStatus deliveryStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_address_id", referencedColumnName = "address_id")
    private Address fromAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_address_id", referencedColumnName = "address_id")
    private Address toAddress;
}
