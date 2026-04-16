package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.DeliveryManNotFoundException;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DispatchOrderUseCase {
    private final DeliveryManRepository deliveryManRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;

    public DispatchOrderUseCase(DeliveryManRepository deliveryManRepository, OrderDeliveryRepository orderDeliveryRepository) {
        this.deliveryManRepository = deliveryManRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
    }

    public void dispatch(UUID orderId, UUID deliverymanId) {
        OrderDelivery order = orderDeliveryRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        DeliveryMan deliveryman = deliveryManRepository.findById(deliverymanId)
                .orElseThrow(() -> new DeliveryManNotFoundException("Delivery Man not found"));

        order.dispatch(deliveryman);
    }
}
