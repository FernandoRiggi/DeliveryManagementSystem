package br.ifsp.demo.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrderHttpRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotBlank(message = "Pickup street is required") @Size(max = 255)
        String pickupStreet,
        @NotBlank(message = "Pickup number is required") @Size(max = 20)
        String pickupNumber,
        @NotBlank(message = "Pickup neighborhood is required") @Size(max = 100)
        String pickupNeighborhood,
        @NotBlank(message = "Pickup city is required") @Size(max = 100)
        String pickupCity,
        @NotBlank(message = "Pickup state is required") @Size(max = 50)
        String pickupState,
        @NotBlank(message = "Pickup country is required") @Size(max = 50)
        String pickupCountry,
        @NotBlank(message = "Pickup CEP is required") @Size(max = 10)
        String pickupCep,

        @NotBlank(message = "Delivery street is required") @Size(max = 255)
        String deliveryStreet,
        @NotBlank(message = "Delivery number is required") @Size(max = 20)
        String deliveryNumber,
        @NotBlank(message = "Delivery neighborhood is required") @Size(max = 100)
        String deliveryNeighborhood,
        @NotBlank(message = "Delivery city is required") @Size(max = 100)
        String deliveryCity,
        @NotBlank(message = "Delivery state is required") @Size(max = 50)
        String deliveryState,
        @NotBlank(message = "Delivery country is required") @Size(max = 50)
        String deliveryCountry,
        @NotBlank(message = "Delivery CEP is required") @Size(max = 10)
        String deliveryCep,

        @NotNull(message = "Distance is required") @Positive(message = "Distance must be greater than zero")
        Double distanceKm
) {}
