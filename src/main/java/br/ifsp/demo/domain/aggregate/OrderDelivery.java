package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderDelivery {
    @Getter
    private UUID id;
    private StatusOrder statusOrder;
    private DeliveryMan deliveryMan;
    @Getter
    private final Address pickupAddress;
    @Getter
    private final Address deliveryAddress;
    private final List<OrderDeliveryEvent> OrderEvents;
    @Getter
    private final Customer customer;

    public OrderDelivery(Customer customer, Address pickupAddress, Address deliveryAddress) {
        if (customer == null) {
            throw new NullPointerException("Customer cannot be null");
        }

        if (pickupAddress == null) {
            throw new NullPointerException("PickupAddress cannot be null");
        }

        if (deliveryAddress == null) {
            throw new NullPointerException("DeliveryAddress cannot be null");
        }

        this.id = UUID.randomUUID();
        this.customer = customer;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.statusOrder = StatusOrder.CREATED;
        OrderEvents = new ArrayList<>();
        OrderEvents.add(new OrderDeliveryEvent(EventType.CREATED));
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

    public void restore(StatusOrder status, DeliveryMan deliveryMan) {
        this.statusOrder = status;
        this.deliveryMan = deliveryMan;
        this.OrderEvents.clear();
    }

    public void restoreId(UUID id) {
        this.id = id;
    }

    public DeliveryMan getDeliveryman() {
        return deliveryMan;
    }
}
