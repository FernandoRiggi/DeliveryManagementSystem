package br.ifsp.demo.application.UseCases;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetOrderUseCase {
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final CustomerRepository customerRepository;


    public GetOrderUseCase(OrderDeliveryRepository orderDeliveryRepository, CustomerRepository customerRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.customerRepository = customerRepository;
    }

    public OrderDelivery findById(UUID orderId) {
        return orderDeliveryRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]"));
    }

    public List<OrderDelivery> findAllOrdersByCustomer(Customer customer) {
        if (customer == null) {
            throw new NullPointerException("Customer cannot be null");
        }

        List<OrderDelivery> orders = orderDeliveryRepository.findAllByCustomer(customer);

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("[Orders not found]");
        }

        return orders;
    }
}
