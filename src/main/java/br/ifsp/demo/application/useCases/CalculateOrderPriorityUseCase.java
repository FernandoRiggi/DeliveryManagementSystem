package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.LogisticScore;

public class CalculateOrderPriorityUseCase {

    private final OrderDeliveryRepository repo;

    public CalculateOrderPriorityUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public LogisticScore calculate(OrderDelivery order, int timeInMinutes){

        int activeCount = repo.findAllActiveOrders(order.getCustomer()).size();

        return LogisticScore.calculate(order.getCustomer().type(),activeCount,order.getDistanceKm(),timeInMinutes);
    }

}
