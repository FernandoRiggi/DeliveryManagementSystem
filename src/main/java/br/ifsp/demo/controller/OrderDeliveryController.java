package br.ifsp.demo.controller;

import br.ifsp.demo.application.useCases.*;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.dto.ErrorResponse;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/orders")
@AllArgsConstructor
public class OrderDeliveryController {
    private final CreateOrderUseCase createOrderUseCase;
    private final CustomerRepository customerRepository;
    private final CalculateOrderPriorityUseCase calculateOrderPriorityUseCase;
    private final CancelRouteUseCase cancelRouteUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final DispatchOrderUseCase dispatchOrderUseCase;
    private final ListCustomerOrdersUseCase listCustomerOrdersUseCase;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderHttpRequest body){
        try {
            Customer customer = customerRepository.findById(body.customerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

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

            Double distanceKm = body.distanceKm();

            CreateOrderRequest request = new CreateOrderRequest(customer.getCustomerId(), pickingAddress, deliveryAddress, distanceKm);
            createOrderUseCase.create(request);

            return ResponseEntity.status(HttpStatus.CREATED).build();
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
}
