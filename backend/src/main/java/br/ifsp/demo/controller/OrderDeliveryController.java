package br.ifsp.demo.controller;

import br.ifsp.demo.application.UseCases.*;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CalculatePriorityRequest;
import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.dto.ErrorResponse;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.LogisticScore;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/orders")
@AllArgsConstructor
public class OrderDeliveryController {
    private final CreateOrderUseCase createOrderUseCase;
    private final CalculateOrderPriorityUseCase calculateOrderPriorityUseCase;
    private final CancelRouteUseCase cancelRouteUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final DispatchOrderUseCase dispatchOrderUseCase;
    private final ListCustomerOrdersUseCase listCustomerOrdersUseCase;
    private final StartRouteUseCase startRouteUseCase;
    private final ConcludeOrderUseCase concludeOrderUseCase;
    private final OrderDeliveryRepository orderDeliveryRepository;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrderHttpRequest body){
        try {
            Address pickingAddress = new Address(
                    body.pickupStreet(),
                    body.pickupNumber(),
                    body.pickupNeighborhood(),
                    body.pickupCity(),
                    body.pickupState(),
                    body.pickupCountry(),
                    new Cep(body.pickupCep())
            );

            Address deliveryAddress = new Address(
                    body.deliveryStreet(),
                    body.deliveryNumber(),
                    body.deliveryNeighborhood(),
                    body.deliveryCity(),
                    body.deliveryState(),
                    body.deliveryCountry(),
                    new Cep(body.deliveryCep())
            );

            CreateOrderRequest request = new CreateOrderRequest(body.customerId(), pickingAddress, deliveryAddress, body.distanceKm());
            OrderDelivery order = createOrderUseCase.create(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("orderId", order.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getById(@PathVariable UUID orderId) {
        try {
            OrderDelivery order = getOrderUseCase.findById(orderId);
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID orderId) {
        try {
            cancelOrderUseCase.cancelOrderDelivery(orderId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/dispatch/{deliverymanId}")
    public ResponseEntity<?> dispatch(
            @PathVariable UUID orderId,
            @PathVariable UUID deliverymanId
    ) {
        try {
            dispatchOrderUseCase.dispatch(orderId, deliverymanId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/cancel-route")
    public ResponseEntity<?> cancelRoute(@PathVariable UUID orderId) {
        try {
            cancelRouteUseCase.cancelRoute(orderId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> listByCustomer(@PathVariable UUID customerId) {
        try {
            return ResponseEntity.ok(listCustomerOrdersUseCase.listAll(customerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/start-route")
    public ResponseEntity<?> startRoute(@PathVariable UUID orderId) {
        try {
            startRouteUseCase.startRoute(orderId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/conclude")
    public ResponseEntity<?> conclude(@PathVariable UUID orderId) {
        try {
            concludeOrderUseCase.conclude(orderId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/queue")
    public ResponseEntity<?> getPriorityQueue() {
        try {
            return ResponseEntity.ok(calculateOrderPriorityUseCase.buildPriorityQueue());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/priority")
    public ResponseEntity<?> calculatePriority(
            @PathVariable UUID orderId,
            @RequestBody CalculatePriorityRequest request
    ) {

        try {

            OrderDelivery order =
                    orderDeliveryRepository.findById(orderId)
                            .orElseThrow(() ->
                                    new RuntimeException("Pedido não encontrado"));

            LogisticScore score =
                    calculateOrderPriorityUseCase.calculate(
                            order,
                            request.waitMinutes()
                    );

            order.setPriorityLevel(score.value());

            orderDeliveryRepository.save(order);

            return ResponseEntity.ok(
                    Map.of(
                            "priority", score.value(),
                            "level", score.getPriorityLevel().name()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
