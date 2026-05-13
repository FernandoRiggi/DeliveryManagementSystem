package br.ifsp.demo.infrastructure.persistence.mapper;

import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEventEntity;

public class OrderDeliveryEventMapper {
    public static OrderDeliveryEventEntity toEntity(OrderDeliveryEvent domain) {
        OrderDeliveryEventEntity entity = new OrderDeliveryEventEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType());
        entity.setDateTime(domain.getDateTime());
        return entity;
    }

    public static OrderDeliveryEvent toDomain(OrderDeliveryEventEntity entity) {
        return OrderDeliveryEvent.reconstitute(
                entity.getId(),
                entity.getType(),
                entity.getDateTime()
        );
    }
}