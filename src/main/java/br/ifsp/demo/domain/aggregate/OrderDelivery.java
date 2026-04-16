package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_delivery")
public class OrderDelivery {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Getter
    @Column(nullable = false)
    private StatusOrder statusOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @JoinColumn(name = "delivery_man_id")
    private DeliveryMan deliveryMan;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "pickup_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "pickup_number", nullable = false)),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "pickup_neighborhood", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "pickup_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country", nullable = false)),
            @AttributeOverride(name = "cep", column = @Column(name = "pickup_cep", nullable = false))
    })
    private Address pickupAddress;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "delivery_number", nullable = false)),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "delivery_neighborhood", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country", nullable = false)),
            @AttributeOverride(name = "cep", column = @Column(name = "delivery_cep", nullable = false))
    })
    private Address deliveryAddress;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @JoinColumn(name = "order_delivery_id")
    private List<OrderDeliveryEvent> orderEvents = new ArrayList<>();

    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Getter
    @Column(nullable = false)
    private double distanceKm;

    public OrderDelivery(Customer customer, Address pickupAddress, Address deliveryAddress, double distanceKm) {
        if (customer == null) {
            throw new NullPointerException("Customer cannot be null");
        }

        if (pickupAddress == null) {
            throw new NullPointerException("PickupAddress cannot be null");
        }

        if (deliveryAddress == null) {
            throw new NullPointerException("DeliveryAddress cannot be null");
        }

        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance cannot be zero or negative");
        }

        this.customer = customer;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.statusOrder = StatusOrder.CREATED;
        this.distanceKm = distanceKm;
        this.orderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }

    public List<OrderDeliveryEvent> getEvents() {
        return Collections.unmodifiableList(orderEvents);
    }

    public DeliveryMan getDeliveryman() {
        return deliveryMan;
    }

    public void cancel() {
        if (this.statusOrder == StatusOrder.CONCLUDED) {
            throw new IllegalStateException("[Order already concluded]");
        }

        boolean hasCancellationEvent = orderEvents.stream()
                .anyMatch(event -> event.getType() == EventType.CANCELLATION);

        if (hasCancellationEvent) {
            throw new IllegalStateException("[Order already cancelled]");
        }

        if (this.deliveryMan != null) {
            deliveryMan.setCapacity(deliveryMan.getCapacity() + 1);
        }

        this.statusOrder = StatusOrder.CANCELED;
        orderEvents.add(new OrderDeliveryEvent(EventType.CANCELLATION));
    }

    public void concluded() {
        if (this.statusOrder != StatusOrder.EN_ROUTE) {
            throw new IllegalStateException("[OrderStatus is not EN_ROUTE]");
        }

        this.statusOrder = StatusOrder.CONCLUDED;
        orderEvents.add(new OrderDeliveryEvent(EventType.CONCLUDED));
    }

    public void startRoute() {
        if (this.statusOrder != StatusOrder.DISPATCHED) {
            throw new IllegalStateException("[OrderStatus is not DISPATCHED]");
        }

        this.statusOrder = StatusOrder.EN_ROUTE;
        orderEvents.add(new OrderDeliveryEvent(EventType.EN_ROUTE));
    }

    public void dispatch(DeliveryMan deliveryMan) {
        if (deliveryMan == null) {
            throw new IllegalStateException("[Deliveryman null]");
        }

        if (this.statusOrder == StatusOrder.CANCELED) {
            throw new IllegalStateException("[Order already cancelled]");
        }

        if (this.statusOrder == StatusOrder.DISPATCHED) {
            throw new IllegalStateException("[Order already dispatched]");
        }

        if (this.statusOrder == StatusOrder.CONCLUDED) {
            throw new IllegalStateException("[Order already concluded]");
        }

        if (deliveryMan.getCapacity() <= 0) {
            throw new IllegalStateException(String.format("[%s] is not enough capacity", deliveryMan.getName()));
        }

        this.statusOrder = StatusOrder.DISPATCHED;
        this.deliveryMan = deliveryMan;
        deliveryMan.setCapacity(deliveryMan.getCapacity() - 1);
        orderEvents.add(new OrderDeliveryEvent(EventType.DISPATCHED));
    }

    public void startDelivery() {
        if (this.statusOrder != StatusOrder.DISPATCHED) {
            throw new IllegalStateException("Order must be dispatched to start delivery");
        }

        this.statusOrder = StatusOrder.EN_ROUTE;
    }

    public void cancelRoute() {
        if (this.statusOrder != StatusOrder.DISPATCHED
                && this.statusOrder != StatusOrder.EN_ROUTE) {
            throw new IllegalStateException("[OrderStatus is not DISPATCHED or EN_ROUTE]");
        }

        if (this.deliveryMan != null) {
            this.deliveryMan.setCapacity(this.deliveryMan.getCapacity() + 1);
        }

        this.deliveryMan = null;
        this.statusOrder = StatusOrder.CREATED;
        orderEvents.add(new OrderDeliveryEvent(EventType.ROUTE_CANCELED));
        orderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
    }
}