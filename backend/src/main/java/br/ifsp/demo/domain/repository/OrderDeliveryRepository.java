package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderDeliveryRepository {
    void save(OrderDelivery order);
    Optional<OrderDelivery> findById(UUID id);
    List<OrderDelivery> findAllByCustomer(Customer customer);
    List<OrderDelivery> findAllActiveOrders(Customer customer);
    List<OrderDelivery> findAll();
}