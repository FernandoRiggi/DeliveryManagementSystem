package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;

import java.util.Optional;
import java.util.UUID;

public class CancelOrderUseCase {
    private final OrderDeliveryRepository repo;

    public CancelOrderUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public void cancelOrderDelivery(UUID OrderId) {
        Optional<OrderDelivery> order = Optional.ofNullable(repo.findById(OrderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]")));
    }
}
