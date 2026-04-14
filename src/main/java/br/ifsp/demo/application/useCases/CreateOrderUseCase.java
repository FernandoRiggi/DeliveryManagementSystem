package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;


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

        if (!customerRepository.exists(request.customer())) {
            throw new IllegalArgumentException("Customer not found");
        }

        OrderDelivery order = new OrderDelivery(
                request.customer(),
                request.pickingAddress(),
                request.deliveryAddress()
        );
        repo.save(order);
    }
}
