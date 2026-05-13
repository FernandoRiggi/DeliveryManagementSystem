package br.ifsp.demo.infrastructure.persistence.mapper;

import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.infrastructure.persistence.entity.AddressEmbeddable;

public class AddressMapper {

    public static AddressEmbeddable toEmbeddable(Address domain) {
        AddressEmbeddable e = new AddressEmbeddable();
        e.setStreet(domain.getStreet());
        e.setNumber(domain.getNumber());
        e.setNeighborhood(domain.getNeighborhood());
        e.setCity(domain.getCity());
        e.setState(domain.getState());
        e.setCountry(domain.getCountry());
        e.setCep(domain.getCep());
        return e;
    }

    public static Address toDomain(AddressEmbeddable entity) {
        return Address.reconstitute(
                entity.getStreet(),
                entity.getNumber(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getCountry(),
                entity.getCep()
        );
    }
}