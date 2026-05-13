package br.ifsp.demo.infrastructure.persistence.mapper;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerEntity;

public class CustomerMapper {
    public static CustomerEntity toEntity(Customer domain) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCustomerId(domain.getCustomerId());
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        return entity;
    }

    public static Customer toDomain(CustomerEntity entity) {
        return Customer.reconstitute(
                entity.getCustomerId(),
                entity.getName(),
                entity.getType()
        );
    }
}