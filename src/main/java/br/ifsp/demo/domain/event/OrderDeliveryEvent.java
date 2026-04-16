package br.ifsp.demo.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_delivery_event")
public class OrderDeliveryEvent {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Getter
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    @Getter
    private LocalDateTime dateTime;

    public OrderDeliveryEvent(EventType type) {
        this.type = type;
        this.dateTime = LocalDateTime.now();
    }

    public OrderDeliveryEvent(EventType type, LocalDateTime dateTime) {
        this.type = type;
        this.dateTime = dateTime;
    }
}