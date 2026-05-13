package br.ifsp.demo.infrastructure.persistence.mapper;

import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.infrastructure.persistence.entity.DeliveryManEntity;

public class DeliveryManMapper {
    public static DeliveryManEntity toEntity(DeliveryMan domain) {
        DeliveryManEntity entity = new DeliveryManEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCapacity(domain.getCapacity());
        return entity;
    }

    public static DeliveryMan toDomain(DeliveryManEntity entity) {
        return DeliveryMan.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getCapacity()
        );
    }
}