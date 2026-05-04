package br.ifsp.demo.infraestrutura.persistence.mapper;

import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.infraestrutura.persistence.entity.DeliveryManEntity;

public class DeliveryManMapper {
    public static DeliveryManEntity toEntity(DeliveryMan domain) {
        if (domain == null) return null;

        DeliveryManEntity entity = new DeliveryManEntity();
        entity.setName(domain.getName());
        entity.setCapacity(domain.getCapacity());

        return entity;
    }

    public static DeliveryMan toDomain(DeliveryManEntity entity) {
        if (entity == null) return null;

        return new DeliveryMan(
                entity.getId(),
                entity.getName(),
                entity.getCapacity()
        );
    }
}
