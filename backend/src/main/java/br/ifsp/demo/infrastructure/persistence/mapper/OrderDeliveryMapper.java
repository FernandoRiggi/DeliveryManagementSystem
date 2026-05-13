package br.ifsp.demo.infrastructure.persistence.mapper;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEntity;

import java.util.List;

public class OrderDeliveryMapper {
    public static OrderDeliveryEntity toEntity(OrderDelivery domain) {
        OrderDeliveryEntity entity = new OrderDeliveryEntity();
        entity.setId(domain.getId());
        entity.setStatusOrder(domain.getStatus());
        entity.setCustomer(CustomerMapper.toEntity(domain.getCustomer()));
        if (domain.getDeliveryman() != null)
            entity.setDeliveryMan(DeliveryManMapper.toEntity(domain.getDeliveryman()));
        entity.setPickupAddress(AddressMapper.toEmbeddable(domain.getPickupAddress()));
        entity.setDeliveryAddress(AddressMapper.toEmbeddable(domain.getDeliveryAddress()));
        entity.setDistanceKm(domain.getDistanceKm());
        entity.setOrderEvents(domain.getEvents().stream()
                .map(OrderDeliveryEventMapper::toEntity)
                .toList());
        return entity;
    }

    public static OrderDelivery toDomain(OrderDeliveryEntity entity) {
        Customer customer = CustomerMapper.toDomain(entity.getCustomer());
        DeliveryMan deliveryMan = entity.getDeliveryMan() != null
                ? DeliveryManMapper.toDomain(entity.getDeliveryMan())
                : null;
        Address pickupAddress = AddressMapper.toDomain(entity.getPickupAddress());
        Address deliveryAddress = AddressMapper.toDomain(entity.getDeliveryAddress());
        List<OrderDeliveryEvent> events = entity.getOrderEvents().stream()
                .map(OrderDeliveryEventMapper::toDomain)
                .toList();

        return OrderDelivery.reconstitute(
                entity.getId(),
                entity.getStatusOrder(),
                customer,
                deliveryMan,
                pickupAddress,
                deliveryAddress,
                entity.getDistanceKm(),
                events
        );
    }
}