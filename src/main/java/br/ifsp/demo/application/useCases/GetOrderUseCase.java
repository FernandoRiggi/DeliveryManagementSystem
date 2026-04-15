package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;

import java.util.Optional;
import java.util.UUID;

public class GetOrderUseCase {
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final CustomerRepository customerRepository;


    public GetOrderUseCase(OrderDeliveryRepository orderDeliveryRepository, CustomerRepository customerRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.customerRepository = customerRepository;
    }

    OrderDelivery findById(UUID orderId) {
        return orderDeliveryRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("[OrderDelivery Not Found]"));
    }

}
