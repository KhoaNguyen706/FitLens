package com.example.fitlens.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record WeightLogResponse(
        Long id,
        BigDecimal weightKg,
        Instant loggedAt,
        String notes,
        Instant createdAt
) {
}
