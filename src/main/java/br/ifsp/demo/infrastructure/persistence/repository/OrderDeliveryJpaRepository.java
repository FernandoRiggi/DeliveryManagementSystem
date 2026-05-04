package br.ifsp.demo.infrastructure.persistence.repository;

import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerEntity;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderDeliveryJpaRepository extends JpaRepository<OrderDeliveryEntity, UUID> {
    List<OrderDeliveryEntity> findAllByCustomer(CustomerEntity customer);

    @Query("""
        select o
        from OrderDeliveryEntity o
        where o.customer = :customer
          and o.statusOrder not in :statuses
    """)
    List<OrderDeliveryEntity> findAllActiveOrders(
            @Param("customer") CustomerEntity customer,
            @Param("statuses") List<StatusOrder> statuses
    );
}