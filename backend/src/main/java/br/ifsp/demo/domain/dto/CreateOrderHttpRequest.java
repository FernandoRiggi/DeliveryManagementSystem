package br.ifsp.demo.domain.dto;

import java.util.UUID;

public record CreateOrderHttpRequest(
        UUID customerId,

        String pickupStreet,
        String pickupNumber,
        String pickupNeighborhood,
        String pickupCity,
        String pickupState,
        String pickupCountry,
        String pickupCep,

        String deliveryStreet,
        String deliveryNumber,
        String deliveryNeighborhood,
        String deliveryCity,
        String deliveryState,
        String deliveryCountry,
        String deliveryCep,

        Double distanceKm
) {
}
