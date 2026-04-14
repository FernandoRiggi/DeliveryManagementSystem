package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;


public class CreateOrderUseCase {
    private final OrderDeliveryRepository repo;

    public CreateOrderUseCase(OrderDeliveryRepository repo) { this.repo = repo; }

    public void create(CreateOrderRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }

        OrderDelivery order = new OrderDelivery(
                request.customer(),
                request.pickingAddress(),
                request.deliveryAddress()
        );
        repo.save(order);
    }
}
