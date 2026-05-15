package br.ifsp.demo.domain.dto;

import br.ifsp.demo.domain.valueObject.CustomerType;

import java.util.UUID;

public record OrderQueueItemResponse(
        UUID orderId,
        String customerName,
        CustomerType customerType,
        double distanceKm,
        int score,
        String priorityLevel,
        long waitMinutes
) {}