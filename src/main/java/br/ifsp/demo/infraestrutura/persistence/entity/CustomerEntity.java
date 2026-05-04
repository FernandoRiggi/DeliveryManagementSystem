package br.ifsp.demo.infraestrutura.persistence.entity;

import br.ifsp.demo.domain.valueObject.CustomerType;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    public CustomerEntity() {}

    public CustomerEntity(String name, CustomerType type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CustomerType getType() { return type; }
    public void setType(CustomerType type) { this.type = type; }
}