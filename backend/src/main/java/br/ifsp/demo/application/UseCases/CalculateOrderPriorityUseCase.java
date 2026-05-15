package br.ifsp.demo.application.UseCases;

import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.dto.OrderQueueItemResponse;
import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.LogisticScore;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalculateOrderPriorityUseCase {

    private final OrderDeliveryRepository repo;
    private final GetPriorityQueueUseCase getPriorityQueueUseCase = new GetPriorityQueueUseCase();

    public CalculateOrderPriorityUseCase(OrderDeliveryRepository repo) {
        this.repo = repo;
    }

    public LogisticScore calculate(OrderDelivery order, int timeInMinutes){
        if(order == null) {
            throw new NullPointerException("Order cannot be null");
        }
        if(order.getStatus() != StatusOrder.CREATED){
            throw new IllegalStateException("Priority can only be calculated in orders with status CREATED");
        }

        int activeCount = repo.findAllActiveOrders(order.getCustomer()).size();

        return LogisticScore.calculate(order.getCustomer().getType(),activeCount,order.getDistanceKm(),timeInMinutes);
    }

    public List<OrderDelivery> getPriorityQueue(List<OrderDelivery> orders, Map<OrderDelivery, Integer> timeInMinutes) {
        Map<OrderDelivery, LogisticScore> scores = orders.stream()
                .filter(o -> o.getStatus() == StatusOrder.CREATED)
                .collect(Collectors.toMap(o -> o, o -> calculate(o, timeInMinutes.getOrDefault(o, 0))));
        return getPriorityQueueUseCase.execute(orders, scores);
    }

    public List<LogisticScore> recalculateQueue(OrderDelivery triggerOrder, Map<OrderDelivery, Integer> timeInMinutes) {
        return repo
                .findAllActiveOrders(triggerOrder.getCustomer())
                .stream()
                .map(o -> calculate(o, timeInMinutes.getOrDefault(o, 0)))
                .toList();
    }

    public List<OrderQueueItemResponse> buildPriorityQueue() {
        List<OrderDelivery> allOrders = repo.findAll();

        Map<OrderDelivery, Integer> timeMap = new HashMap<>();
        for (OrderDelivery order : allOrders) {
            if (order.getStatus() == StatusOrder.CREATED) {
                timeMap.put(order, calculateWaitMinutes(order));
            }
        }

        return getPriorityQueue(allOrders, timeMap).stream()
                .map(order -> {
                    int wait = timeMap.getOrDefault(order, 0);
                    LogisticScore score = calculate(order, wait);
                    return new OrderQueueItemResponse(
                            order.getId(),
                            order.getCustomer().getName(),
                            order.getCustomer().getType(),
                            order.getDistanceKm(),
                            score.value(),
                            score.getPriorityLevel().name(),
                            wait
                    );
                })
                .toList();
    }

    private int calculateWaitMinutes(OrderDelivery order) {
        return order.getEvents().stream()
                .filter(e -> e.getType() == EventType.CREATED)
                .reduce((a, b) -> b)
                .map(e -> (int) Duration.between(e.getDateTime(), LocalDateTime.now()).toMinutes())
                .orElse(0);
    }

}
