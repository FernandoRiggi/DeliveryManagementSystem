package br.ifsp.demo.application;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderUseCase {
    private final OrderDeliveryRepository repo;
    private final CustomerRepository customerRepository;

    public CreateOrderUseCase(
            OrderDeliveryRepository repo,
            CustomerRepository customerRepository
    ) {
        this.repo = repo;
        this.customerRepository = customerRepository;
    }

    public void create(CreateOrderRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        OrderDelivery order = new OrderDelivery(
                customer,
                request.pickingAddress(),
                request.deliveryAddress(),
                request.distanceKm()
        );
        repo.save(order);
    }
}
