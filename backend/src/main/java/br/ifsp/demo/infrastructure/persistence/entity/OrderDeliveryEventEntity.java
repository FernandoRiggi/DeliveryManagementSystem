package br.ifsp.demo.infrastructure.persistence.entity;

import br.ifsp.demo.domain.event.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_delivery_event")
@Getter
@Setter
@NoArgsConstructor
public class OrderDeliveryEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private LocalDateTime dateTime;
}