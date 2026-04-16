package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.DeliveryMan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManRepository extends JpaRepository<DeliveryMan, UUID> {
}
