package com.example.fitlens.integration;

import com.example.fitlens.domain.enums.MealType;
import com.example.fitlens.integration.support.IntegrationTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class MealEntryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        String email = "meal-user-" + UUID.randomUUID() + "@test.com";
        accessToken = IntegrationTestSupport.registerAndLogin(
                mockMvc,
                objectMapper,
                email,
                "password123",
                "Meal User"
        );
    }

    @Test
    void createMealAndFetchDashboard_returnsDailyTotal() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mealName": "Chicken rice bowl",
                                  "mealType": "%s",
                                  "calories": 750,
                                  "loggedAt": "2026-07-01T12:30:00Z"
                                }
                                """.formatted(MealType.LUNCH)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.calories").value(750));

        mockMvc.perform(get("/api/dashboard/today")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("day", "2026-07-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCalories").value(750))
                .andExpect(jsonPath("$.data.meals", hasSize(1)));
    }

    @Test
    void getMissingMeal_returnsNotFoundApiResponse() throws Exception {
        mockMvc.perform(get("/api/meals/99999")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteMeal_returnsSuccessApiResponse() throws Exception {
        String createResponse = mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mealName": "Greek yogurt",
                                  "mealType": "BREAKFAST",
                                  "calories": 320,
                                  "loggedAt": "2026-07-01T08:15:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long mealId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(delete("/api/meals/" + mealId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meal entry deleted successfully"));
    }
}
