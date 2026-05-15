package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderDelivery {

    @Getter
    private final UUID id;

    private StatusOrder statusOrder;

    private DeliveryMan deliveryMan;

    @Getter
    private final Address pickupAddress;

    @Getter
    private final Address deliveryAddress;

    private List<OrderDeliveryEvent> orderEvents = new ArrayList<>();

    @Getter
    private final Customer customer;

    @Getter
    private final double distanceKm;

    @Getter
    @Setter
    private Integer priorityLevel;

    public OrderDelivery(
            Customer customer,
            Address pickupAddress,
            Address deliveryAddress,
            double distanceKm
    ) {

        if (customer == null)
            throw new NullPointerException("Customer cannot be null");

        if (pickupAddress == null)
            throw new NullPointerException("PickupAddress cannot be null");

        if (deliveryAddress == null)
            throw new NullPointerException("DeliveryAddress cannot be null");

        if (distanceKm <= 0)
            throw new IllegalArgumentException("Distance cannot be zero or negative");

        this.id = UUID.randomUUID();
        this.customer = customer;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.statusOrder = StatusOrder.CREATED;
        this.distanceKm = distanceKm;
        this.priorityLevel = null;

        this.orderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
    }

    private OrderDelivery(
            UUID id,
            StatusOrder statusOrder,
            Customer customer,
            DeliveryMan deliveryMan,
            Address pickupAddress,
            Address deliveryAddress,
            double distanceKm,
            Integer priorityLevel,
            List<OrderDeliveryEvent> events
    ) {

        this.id = id;
        this.statusOrder = statusOrder;
        this.customer = customer;
        this.deliveryMan = deliveryMan;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.distanceKm = distanceKm;
        this.priorityLevel = priorityLevel;

        this.orderEvents = new ArrayList<>(events);
    }

    public static OrderDelivery reconstitute(
            UUID id,
            StatusOrder statusOrder,
            Customer customer,
            DeliveryMan deliveryMan,
            Address pickupAddress,
            Address deliveryAddress,
            double distanceKm,
            Integer priorityLevel,
            List<OrderDeliveryEvent> events
    ) {

        return new OrderDelivery(
                id,
                statusOrder,
                customer,
                deliveryMan,
                pickupAddress,
                deliveryAddress,
                distanceKm,
                priorityLevel,
                events
        );
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

        if (this.statusOrder == StatusOrder.CONCLUDED)
            throw new IllegalStateException("[Order already concluded]");

        boolean hasCancellationEvent = orderEvents.stream()
                .anyMatch(event -> event.getType() == EventType.CANCELLATION);

        if (hasCancellationEvent)
            throw new IllegalStateException("[Order already cancelled]");

        if (this.deliveryMan != null)
            deliveryMan.increaseCapacity();

        this.statusOrder = StatusOrder.CANCELED;

        orderEvents.add(new OrderDeliveryEvent(EventType.CANCELLATION));
    }

    public void concluded() {

        if (this.statusOrder != StatusOrder.EN_ROUTE)
            throw new IllegalStateException("[OrderStatus is not EN_ROUTE]");

        this.statusOrder = StatusOrder.CONCLUDED;

        orderEvents.add(new OrderDeliveryEvent(EventType.CONCLUDED));
    }

    public void startRoute() {

        if (this.statusOrder != StatusOrder.DISPATCHED)
            throw new IllegalStateException("[OrderStatus is not DISPATCHED]");

        this.statusOrder = StatusOrder.EN_ROUTE;

        orderEvents.add(new OrderDeliveryEvent(EventType.EN_ROUTE));
    }

    public void dispatch(DeliveryMan deliveryMan) {

        validateDispatchable(deliveryMan);

        this.statusOrder = StatusOrder.DISPATCHED;
        this.deliveryMan = deliveryMan;

        deliveryMan.decreaseCapacity();

        orderEvents.add(new OrderDeliveryEvent(EventType.DISPATCHED));
    }

    private void validateDispatchable(DeliveryMan deliveryMan) {

        if (deliveryMan == null)
            throw new IllegalStateException("[Deliveryman null]");

        if (this.statusOrder == StatusOrder.CANCELED)
            throw new IllegalStateException("[Order already cancelled]");

        if (this.statusOrder == StatusOrder.DISPATCHED)
            throw new IllegalStateException("[Order already dispatched]");

        if (this.statusOrder == StatusOrder.CONCLUDED)
            throw new IllegalStateException("[Order already concluded]");

        if (deliveryMan.getCapacity() <= 0)
            throw new IllegalStateException(
                    String.format("[%s] is not enough capacity", deliveryMan.getName())
            );
    }

    public void cancelRoute() {

        if (
                this.statusOrder != StatusOrder.DISPATCHED
                        && this.statusOrder != StatusOrder.EN_ROUTE
        ) {
            throw new IllegalStateException(
                    "[OrderStatus is not DISPATCHED or EN_ROUTE]"
            );
        }

        if (this.deliveryMan != null)
            this.deliveryMan.increaseCapacity();

        this.deliveryMan = null;
        this.statusOrder = StatusOrder.CREATED;

        orderEvents.add(new OrderDeliveryEvent(EventType.ROUTE_CANCELED));
        orderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
    }
}