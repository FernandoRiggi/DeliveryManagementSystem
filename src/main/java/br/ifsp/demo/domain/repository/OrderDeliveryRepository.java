package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import java.util.Optional;
import java.util.UUID;

public interface OrderDeliveryRepository {
    Optional<OrderDelivery> findById(UUID orderId);
    void save(OrderDelivery orderDelivery);
}
