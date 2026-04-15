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

    private String pickupStreet;
    private String pickupCity;

    private String deliveryStreet;
    private String deliveryCity;

    public OrderDeliveryEntity() {}
}