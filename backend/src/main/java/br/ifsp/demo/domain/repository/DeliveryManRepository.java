package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.DeliveryMan;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManRepository {
    void save(DeliveryMan deliveryMan);
    Optional<DeliveryMan> findById(UUID id);
}