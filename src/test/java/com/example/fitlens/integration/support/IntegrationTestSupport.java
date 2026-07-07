package com.example.fitlens.integration.support;

import com.example.fitlens.dto.request.LoginRequest;
import com.example.fitlens.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class IntegrationTestSupport {

    private IntegrationTestSupport() {
    }

    public static String registerAndLogin(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String email,
            String password,
            String displayName
    ) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(email, password, displayName);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        return login(mockMvc, objectMapper, email, password);
    }

    public static String login(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password)
            throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("token").asText();
    }
}
