package br.ifsp.demo.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery_man")
public class DeliveryMan {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(nullable = false)
    private int capacity;

    public DeliveryMan(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
