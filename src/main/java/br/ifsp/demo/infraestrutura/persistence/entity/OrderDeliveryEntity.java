package br.ifsp.demo.infraestrutura.persistence.entity;

import br.ifsp.demo.domain.aggregate.StatusOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderDeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryman_id")
    private DeliveryManEntity deliveryMan;

    // PICKUP
    private String pickupStreet;
    private String pickupNumber;
    private String pickupNeighborhood;
    private String pickupCity;
    private String pickupState;
    private String pickupCountry;
    private String pickupCep;

    // DELIVERY
    private String deliveryStreet;
    private String deliveryNumber;
    private String deliveryNeighborhood;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryCountry;
    private String deliveryCep;

    public OrderDeliveryEntity() {}


}