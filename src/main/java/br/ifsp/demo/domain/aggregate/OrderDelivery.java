package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDelivery {
    private StatusOrder statusOrder;
    private Deliveryman deliveryMan;
    private final List<OrderDeliveryEvent> OrderEvents;

    public OrderDelivery(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
        OrderEvents = new ArrayList<>();
    }


    public void cancel() {
        if(this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");
        boolean hasCancellationEvent = OrderEvents.stream().anyMatch(event -> event.getType() == EventType.CANCELLATION);
        if(hasCancellationEvent) throw new IllegalStateException("[Order already cancelled]");

        this.statusOrder = StatusOrder.CANCELED;
        OrderEvents.add(new OrderDeliveryEvent(EventType.CANCELLATION));
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }

    //Refatorar o código, cada status deve ter sua função, setStatus nao deve existir
    public void setStatusOrder(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }

    public List<OrderDeliveryEvent> getEvents() {
        return Collections.unmodifiableList(OrderEvents);
    }

    public void dispatch(Deliveryman deliveryMan) {
        if(deliveryMan == null) throw new IllegalStateException("[Deliveryman null]");
        if(this.statusOrder == StatusOrder.CANCELED) throw new IllegalStateException("[Order already cancelled]");
        if(this.statusOrder == StatusOrder.DISPATCHED)  throw new IllegalStateException("[Order already dispatched]");
        if (this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");

        if(deliveryMan.getCapacity() <= 0)
            throw new IllegalStateException(String.format("[%s] is not enough capacity", deliveryMan.getName()));
        this.statusOrder = StatusOrder.DISPATCHED;
        this.deliveryMan = deliveryMan;

        OrderEvents.add(new OrderDeliveryEvent(EventType.DISPATCHED));
    }

    public Deliveryman getDeliveryman() {
        return deliveryMan;
    }
}
