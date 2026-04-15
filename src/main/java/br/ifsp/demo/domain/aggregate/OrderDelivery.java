package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDelivery {
    private StatusOrder statusOrder;
    private DeliveryMan deliveryMan;
    @Getter
    private final Address pickupAddress;
    @Getter
    private final Address deliveryAddress;
    private final List<OrderDeliveryEvent> OrderEvents;
    @Getter
    private final Customer customer;
    @Getter
    private final double distanceKm;

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
        OrderEvents = new ArrayList<>();
        OrderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
        this.distanceKm = distanceKm;
    }


    public void cancel() {
        if(this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");
        boolean hasCancellationEvent = OrderEvents.stream().anyMatch(event -> event.getType() == EventType.CANCELLATION);
        if(hasCancellationEvent) throw new IllegalStateException("[Order already cancelled]");

        if (this.deliveryMan != null) {
            deliveryMan.setCapacity(deliveryMan.getCapacity() + 1);
        }

        this.statusOrder = StatusOrder.CANCELED;
        OrderEvents.add(new OrderDeliveryEvent(EventType.CANCELLATION));
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }

    public List<OrderDeliveryEvent> getEvents() {
        return Collections.unmodifiableList(OrderEvents);
    }

    public void concluded(){
        if (this.statusOrder != StatusOrder.EN_ROUTE) throw new IllegalStateException("[OrderStatus is not EN_ROUTE]");

        this.statusOrder = StatusOrder.CONCLUDED;
        OrderEvents.add(new OrderDeliveryEvent(EventType.CONCLUDED));
    }

    public void startRoute(){
        if(this.statusOrder != StatusOrder.DISPATCHED) throw new IllegalStateException("[OrderStatus is not DISPATCHED]");
        this.statusOrder = StatusOrder.EN_ROUTE;

        OrderEvents.add(new OrderDeliveryEvent(EventType.EN_ROUTE));
    }

    public void dispatch(DeliveryMan deliveryMan) {
        if(deliveryMan == null) throw new IllegalStateException("[Deliveryman null]");
        if(this.statusOrder == StatusOrder.CANCELED) throw new IllegalStateException("[Order already cancelled]");
        if(this.statusOrder == StatusOrder.DISPATCHED)  throw new IllegalStateException("[Order already dispatched]");
        if (this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");

        if(deliveryMan.getCapacity() <= 0)
            throw new IllegalStateException(String.format("[%s] is not enough capacity", deliveryMan.getName()));

        this.statusOrder = StatusOrder.DISPATCHED;
        this.deliveryMan = deliveryMan;
        deliveryMan.setCapacity(deliveryMan.getCapacity() - 1);
        OrderEvents.add(new OrderDeliveryEvent(EventType.DISPATCHED));
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
        OrderEvents.add(new OrderDeliveryEvent(EventType.ROUTE_CANCELED));
        OrderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
    }

    public DeliveryMan getDeliveryman() {
        return deliveryMan;
    }
}
