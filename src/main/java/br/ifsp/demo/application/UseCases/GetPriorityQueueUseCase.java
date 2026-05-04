package br.ifsp.demo.application;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.valueObject.LogisticScore;

import java.util.List;
import java.util.Map;


public class GetPriorityQueueUseCase {
    public List<OrderDelivery> execute(List<OrderDelivery> orders, Map<OrderDelivery, LogisticScore> scores) {
        return orders.stream()
                .filter(o -> o.getStatus() == StatusOrder.CREATED)
                .sorted((o1, o2) ->
                        Integer.compare(
                                scores.get(o2).value(),
                                scores.get(o1).value()
                        )
                )
                .toList();
    }
}

