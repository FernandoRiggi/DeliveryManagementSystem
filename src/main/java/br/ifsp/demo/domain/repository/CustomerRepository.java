package br.ifsp.demo.domain.repository;

import br.ifsp.demo.domain.aggregate.Customer;

public interface CustomerRepository {
    boolean exists(Customer customer);
}
