package br.ifsp.demo.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderDeliveryEvent {
    private Long id;
    private EventType type;
    private LocalDateTime dateTime;

    private OrderDeliveryEvent() {}

    public OrderDeliveryEvent(EventType type) {
        this.type = type;
        this.dateTime = LocalDateTime.now();
    }

    public OrderDeliveryEvent(EventType type, LocalDateTime dateTime) {
        this.type = type;
        this.dateTime = dateTime;
    }

    public static OrderDeliveryEvent reconstitute(Long id, EventType type, LocalDateTime dateTime) {
        OrderDeliveryEvent e = new OrderDeliveryEvent();
        e.id = id;
        e.type = type;
        e.dateTime = dateTime;
        return e;
    }
}