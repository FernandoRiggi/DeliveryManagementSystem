package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConcludeOrderUseCase {
    private final OrderDeliveryRepository repo;

    public ConcludeOrderUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public void conclude(UUID orderId) {
        OrderDelivery order = repo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]"));

        order.concluded();
        repo.save(order);
    }
}