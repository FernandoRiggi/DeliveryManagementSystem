package br.ifsp.demo.application.useCases;


import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;

import java.util.UUID;

public class CancelRouteUseCase {
    private final OrderDeliveryRepository repo;

    public CancelRouteUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public void cancelRoute(UUID orderId) {
        OrderDelivery order = repo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]"));

        order.cancelRoute();
        repo.save(order);
    }
}
