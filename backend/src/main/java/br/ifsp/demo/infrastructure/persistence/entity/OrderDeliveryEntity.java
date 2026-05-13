package br.ifsp.demo.infrastructure.persistence.entity;

import br.ifsp.demo.domain.aggregate.StatusOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "order_delivery")
@Getter
@Setter
@NoArgsConstructor
public class OrderDeliveryEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrder statusOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_man_id")
    private DeliveryManEntity deliveryMan;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "pickup_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "pickup_number", nullable = false)),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "pickup_neighborhood", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "pickup_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country",  nullable = false)),
            @AttributeOverride(name = "cep", column = @Column(name = "pickup_cep", nullable = false))
    })
    private AddressEmbeddable pickupAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "delivery_number", nullable = false)),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "delivery_neighborhood",
                    nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country", nullable = false)),
            @AttributeOverride(name = "cep", column = @Column(name = "delivery_cep", nullable = false))
    })
    private AddressEmbeddable deliveryAddress;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_delivery_id")
    private List<OrderDeliveryEventEntity> orderEvents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(nullable = false)
    private double distanceKm;
}