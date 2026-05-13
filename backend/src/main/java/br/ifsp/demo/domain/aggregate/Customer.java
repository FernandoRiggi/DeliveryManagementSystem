package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.valueObject.CustomerType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Customer {
    private UUID customerId;
    private String name;
    private CustomerType type;

    private Customer() {}

    public Customer(String name, CustomerType type) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null");

        this.customerId = UUID.randomUUID();
        this.name = name;
        this.type = type;
    }

    public static Customer reconstitute(UUID customerId, String name, CustomerType type) {
        Customer c = new Customer();
        c.customerId = customerId;
        c.name = name;
        c.type = type;
        return c;
    }
}
