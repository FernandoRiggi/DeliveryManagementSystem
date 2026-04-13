package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.domain.valueObject.CustomerType;

public record Customer(String name, CustomerType type) {
    public Customer{
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
    }
}
