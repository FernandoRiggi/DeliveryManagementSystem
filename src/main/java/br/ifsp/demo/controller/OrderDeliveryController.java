package br.ifsp.demo.controller;

import br.ifsp.demo.application.useCases.CreateOrderUseCase;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.dto.ErrorResponse;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderDeliveryController {
    private final CreateOrderUseCase createOrderUseCase;

    public OrderDeliveryController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderHttpRequest body){
        try {
            Customer customer = new Customer(
                    body.customerName(),
                    CustomerType.valueOf(body.customerType().toUpperCase())
            );

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

            CreateOrderRequest request = new CreateOrderRequest(customer, pickingAddress, deliveryAddress, distanceKm);
            createOrderUseCase.create(request);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
