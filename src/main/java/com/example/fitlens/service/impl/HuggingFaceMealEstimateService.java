package com.example.fitlens.service.impl;

import com.example.fitlens.config.AiProperties;
import com.example.fitlens.domain.enums.MealType;
import com.example.fitlens.dto.request.EstimateMealRequest;
import com.example.fitlens.dto.response.MealEstimateResponse;
import com.example.fitlens.service.MealEstimateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceMealEstimateService implements MealEstimateService {

    private final AiProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public MealEstimateResponse estimateMeal(EstimateMealRequest request) {
        if (!properties.enabled()) {
            return fallbackEstimate("AI service is currently disabled.");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(properties.url() + "/estimate"))
                    .timeout(Duration.ofSeconds(properties.timeoutSeconds()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(properties.timeoutSeconds()))
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("AI service returned status code {}", response.statusCode());
                return fallbackEstimate("AI service is temporarily unavailable. Status: " + response.statusCode());
            }

            JsonNode node = objectMapper.readTree(response.body());
            return new MealEstimateResponse(
                    text(node, "mealName", "Meal"),
                    mealType(text(node, "mealType", "OTHER")),
                    clamp(node.path("calories").asInt(0), 0, 5000),
                    clamp(node.path("confidencePercent").asInt(0), 0, 100),
                    text(node, "notes", "Hugging Face estimate. Review before saving."),
                    node.path("aiGenerated").asBoolean(true)
            );
        } catch (IOException e) {
            log.error("AI service communication failed", e);
            return fallbackEstimate("AI service failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("AI service request interrupted", e);
            return fallbackEstimate("AI service request was interrupted.");
        }
    }

    private MealEstimateResponse fallbackEstimate(String notes) {
        return new MealEstimateResponse("Meal", MealType.OTHER, 0, 0, notes, false);
    }

    private String text(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            return fallback;
        }
        return value.asText();
    }

    private MealType mealType(String value) {
        try {
            return MealType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return MealType.OTHER;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
