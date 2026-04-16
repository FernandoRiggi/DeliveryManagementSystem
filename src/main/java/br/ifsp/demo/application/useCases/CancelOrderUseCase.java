package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CancelOrderUseCase {
    private final OrderDeliveryRepository repo;

    public CancelOrderUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public void cancelOrderDelivery(UUID OrderId) {
        Optional<OrderDelivery> order = Optional.of(repo.findById(OrderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]")));

        order.ifPresent( o -> {
                    o.cancel();
                    repo.save(o);
                }
        );
    }
}
