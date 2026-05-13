package br.ifsp.demo.domain.dto;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.valueObject.Address;

import java.util.UUID;

public record CreateOrderRequest(UUID customerId, Address pickingAddress, Address deliveryAddress, Double distanceKm) {
}
