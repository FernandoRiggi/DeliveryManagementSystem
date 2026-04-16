package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.valueObject.CustomerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "customer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Customer {
    @Id
    private UUID customerId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    public Customer(String name, CustomerType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        this.customerId = UUID.randomUUID();
        this.name = name;
        this.type = type;
    }
}
