package br.ifsp.demo.application;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StartRouteUseCase {
    private final OrderDeliveryRepository repo;

    public StartRouteUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public void startRoute(UUID orderId) {
        OrderDelivery order = repo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]"));

        order.startRoute();
        repo.save(order);
    }
}

