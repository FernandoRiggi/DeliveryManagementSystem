package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;

import java.util.List;

public class ListCustomerOrdersUseCase {
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final CustomerRepository customerRepository;

    public ListCustomerOrdersUseCase(OrderDeliveryRepository orderDeliveryRepository, CustomerRepository customerRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.customerRepository = customerRepository;
    }

    List<OrderDelivery> listAll(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        return orderDeliveryRepository.findAllByCustomer(customer);
    }

    List<OrderDelivery> listActiveOrders(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() != StatusOrder.CONCLUDED && orderDelivery.getStatus() != StatusOrder.CANCELED)
                .toList();
    }

    List<OrderDelivery> listInactiveOrders(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() == StatusOrder.CONCLUDED || orderDelivery.getStatus() == StatusOrder.CANCELED)
                .toList();
    }

    List<OrderDelivery> listAllCreatedOrders(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        return orderDeliveryRepository.findAllByCustomer(customer)
                .stream()
                .filter(orderDelivery -> orderDelivery.getStatus() == StatusOrder.CREATED)
                .toList();
    }
}
