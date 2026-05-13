package br.ifsp.demo.application.UseCases;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListCustomerOrdersUseCase {
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final CustomerRepository customerRepository;

    public ListCustomerOrdersUseCase(OrderDeliveryRepository orderDeliveryRepository, CustomerRepository customerRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.customerRepository = customerRepository;
    }

    public List<OrderDelivery> listAll(UUID id_customer){
        Customer customer = customerRepository.findById(id_customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return orderDeliveryRepository.findAllByCustomer(customer);
    }

    List<OrderDelivery> listActiveOrders(UUID id_customer){
        Customer customer = customerRepository.findById(id_customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() != StatusOrder.CONCLUDED && orderDelivery.getStatus() != StatusOrder.CANCELED)
                .toList();
    }

    List<OrderDelivery> listInactiveOrders(UUID id_customer){
        Customer customer = customerRepository.findById(id_customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() == StatusOrder.CONCLUDED || orderDelivery.getStatus() == StatusOrder.CANCELED)
                .toList();
    }

    List<OrderDelivery> listAllCreatedOrders(UUID id_customer){
        Customer customer = customerRepository.findById(id_customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() == StatusOrder.CREATED)
                .toList();
    }
}
