package br.ifsp.demo.domain.model.aggregate;

import java.time.LocalDateTime;

public record OrderDeliveryEvent(EventType type, LocalDateTime dateTime) {
    public OrderDeliveryEvent(EventType type) {
        this(type, LocalDateTime.now());
    }

    public EventType getType() {
        return type;
    }
}
