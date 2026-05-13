package br.ifsp.demo.infrastructure.persistence;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.infrastructure.persistence.mapper.CustomerMapper;
import br.ifsp.demo.infrastructure.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Customer customer) {
        jpaRepository.save(CustomerMapper.toEntity(customer));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(CustomerMapper::toDomain);
    }
}