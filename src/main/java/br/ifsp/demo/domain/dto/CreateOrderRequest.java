package br.ifsp.demo.domain.dto;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.valueObject.Address;

public record CreateOrderRequest(Customer customer, Address pickingAddress, Address deliveryAddress, Double distanceKm) {
}
