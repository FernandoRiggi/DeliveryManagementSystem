package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    void save(Customer customer);
    Optional<Customer> findById(UUID id);
}