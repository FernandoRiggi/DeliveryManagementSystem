package br.ifsp.demo.domain.event;

import java.time.LocalDateTime;

public record OrderDeliveryEvent(EventType type, LocalDateTime dateTime) {
    public OrderDeliveryEvent(EventType type) {
        this(type, LocalDateTime.now());
    }

    public EventType getType() {
        return type;
    }
}
