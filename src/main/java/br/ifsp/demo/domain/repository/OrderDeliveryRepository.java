package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, UUID> {
    List<OrderDelivery> findAllByCustomer(Customer customer);
    List<OrderDelivery> findAllActiveOrders(Customer customer);
}
