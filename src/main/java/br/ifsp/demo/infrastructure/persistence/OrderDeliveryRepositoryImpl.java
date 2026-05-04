package br.ifsp.demo.infrastructure.persistence;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerEntity;
import br.ifsp.demo.infrastructure.persistence.mapper.CustomerMapper;
import br.ifsp.demo.infrastructure.persistence.mapper.OrderDeliveryMapper;
import br.ifsp.demo.infrastructure.persistence.repository.CustomerJpaRepository;
import br.ifsp.demo.infrastructure.persistence.repository.OrderDeliveryJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderDeliveryRepositoryImpl implements OrderDeliveryRepository {
    private final OrderDeliveryJpaRepository jpaRepository;
    private final CustomerJpaRepository customerJpaRepository;

    public OrderDeliveryRepositoryImpl(OrderDeliveryJpaRepository jpaRepository,
                                       CustomerJpaRepository customerJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public void save(OrderDelivery order) {
        jpaRepository.save(OrderDeliveryMapper.toEntity(order));
    }

    @Override
    public Optional<OrderDelivery> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderDeliveryMapper::toDomain);
    }

    @Override
    public List<OrderDelivery> findAllByCustomer(Customer customer) {
        CustomerEntity customerEntity = customerJpaRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return jpaRepository.findAllByCustomer(customerEntity).stream()
                .map(OrderDeliveryMapper::toDomain)
                .toList();
    }

    @Override
    public List<OrderDelivery> findAllActiveOrders(Customer customer) {
        CustomerEntity customerEntity = customerJpaRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        List<StatusOrder> inactiveStatuses = List.of(StatusOrder.CANCELED, StatusOrder.CONCLUDED);
        return jpaRepository.findAllActiveOrders(customerEntity, inactiveStatuses).stream()
                .map(OrderDeliveryMapper::toDomain)
                .toList();
    }

    @Override
    public List<OrderDelivery> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderDeliveryMapper::toDomain)
                .toList();
    }
}