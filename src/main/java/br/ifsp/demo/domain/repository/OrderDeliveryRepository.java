package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, UUID> {
    List<OrderDelivery> findAllByCustomer(Customer customer);

    @Query("""
        select o
        from OrderDelivery o
        where o.customer = :customer
          and o.statusOrder not in :statuses
    """)
    List<OrderDelivery> findAllActiveOrders(@Param("customer") Customer customer);
}
