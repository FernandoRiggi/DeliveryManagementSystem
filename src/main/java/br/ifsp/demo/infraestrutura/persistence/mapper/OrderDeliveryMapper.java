package br.ifsp.demo.infraestrutura.persistence.mapper;

import br.ifsp.demo.domain.aggregate.*;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.infraestrutura.persistence.entity.*;

public class OrderDeliveryMapper {

    public static OrderDelivery toDomain(OrderDeliveryEntity entity) {
        if (entity == null) return null;

        Customer customer = CustomerMapper.toDomain(entity.getCustomer());

        Address pickupAddress = new Address(
                entity.getPickupStreet(),
                entity.getPickupNumber(),
                entity.getPickupNeighborhood(),
                entity.getPickupCity(),
                entity.getPickupState(),
                entity.getPickupCountry(),
                new Cep(entity.getPickupCep())
        );

        Address deliveryAddress = new Address(
                entity.getDeliveryStreet(),
                entity.getDeliveryNumber(),
                entity.getDeliveryNeighborhood(),
                entity.getDeliveryCity(),
                entity.getDeliveryState(),
                entity.getDeliveryCountry(),
                new Cep(entity.getDeliveryCep() != null ? entity.getDeliveryCep() : "")
        );

        OrderDelivery order = new OrderDelivery(
                customer,
                pickupAddress,
                deliveryAddress
        );

        DeliveryMan deliveryMan = DeliveryManMapper.toDomain(entity.getDeliveryMan());
        order.restore(entity.getStatus(), deliveryMan);
        order.restoreId(entity.getId());
        return order;
    }
}