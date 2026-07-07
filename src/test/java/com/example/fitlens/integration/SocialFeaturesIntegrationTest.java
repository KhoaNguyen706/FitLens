package com.example.fitlens.integration;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class SocialFeaturesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String userAToken;
    private String userBToken;
    private String userBEmail;

    @BeforeEach
    void setUp() throws Exception {
        userBEmail = "friend-" + UUID.randomUUID() + "@test.com";
        userAToken = IntegrationTestSupport.registerAndLogin(
                mockMvc, objectMapper,
                "user-a-" + UUID.randomUUID() + "@test.com",
                "password123",
                "User A"
        );
        userBToken = IntegrationTestSupport.registerAndLogin(
                mockMvc, objectMapper,
                userBEmail,
                "password123",
                "User B"
        );
    }

    @Test
    void friendRequestAcceptAndFeedPostWithReaction() throws Exception {
        mockMvc.perform(post("/api/friends/requests")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s"}
                                """.formatted(userBEmail)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/friends/requests")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        long requestId = objectMapper.readTree(
                mockMvc.perform(get("/api/friends/requests")
                                .header("Authorization", "Bearer " + userBToken))
                        .andReturn().getResponse().getContentAsString()
        ).path("data").get(0).path("id").asLong();

        mockMvc.perform(post("/api/friends/requests/" + requestId + "/accept")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/friends")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        String mealResponse = mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mealName": "Protein bowl",
                                  "mealType": "LUNCH",
                                  "calories": 520,
                                  "loggedAt": "2026-07-02T12:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long mealId = objectMapper.readTree(mealResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mealEntryId": %d,
                                  "caption": "Great lunch",
                                  "visibility": "CLOSE_FRIENDS"
                                }
                                """.formatted(mealId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].caption").value("Great lunch"));

        long postId = objectMapper.readTree(
                mockMvc.perform(get("/api/feed")
                                .header("Authorization", "Bearer " + userBToken))
                        .andReturn().getResponse().getContentAsString()
        ).path("data").get(0).path("id").asLong();

        mockMvc.perform(post("/api/posts/" + postId + "/reactions")
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"emoji":"❤️"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.myReaction").value("❤️"));
    }
}
