package br.ifsp.demo.domain.model.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDelivery {
    private StatusOrder statusOrder;
    private final List<OrderDeliveryEvent> OrderEvents;

    public OrderDelivery(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
        OrderEvents = new ArrayList<>();
    }


    public void cancel() {
        if(this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");
        this.statusOrder = StatusOrder.CANCELED;
        OrderEvents.add(new OrderDeliveryEvent(EventType.CANCELLATION));
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }

    public void setStatusOrder(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }

    public List<OrderDeliveryEvent> getEvents() {
        return Collections.unmodifiableList(OrderEvents);
    }
}
