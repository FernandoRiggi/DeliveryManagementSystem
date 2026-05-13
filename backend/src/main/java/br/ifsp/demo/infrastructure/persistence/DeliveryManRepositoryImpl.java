package br.ifsp.demo.infrastructure.persistence;

import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.infrastructure.persistence.mapper.DeliveryManMapper;
import br.ifsp.demo.infrastructure.persistence.repository.DeliveryManJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DeliveryManRepositoryImpl implements DeliveryManRepository {
    private final DeliveryManJpaRepository jpaRepository;

    public DeliveryManRepositoryImpl(DeliveryManJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(DeliveryMan deliveryMan) {
        jpaRepository.save(DeliveryManMapper.toEntity(deliveryMan));
    }

    @Override
    public Optional<DeliveryMan> findById(UUID id) {
        return jpaRepository.findById(id).map(DeliveryManMapper::toDomain);
    }
}