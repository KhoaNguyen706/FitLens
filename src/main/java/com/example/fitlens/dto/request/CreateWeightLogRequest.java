package com.example.fitlens.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateWeightLogRequest(
        @NotNull @DecimalMin("0.01") BigDecimal weightKg,
        Instant loggedAt,
        String notes
) {
}
