package com.jobtrack.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUsers() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailsForUnknownUser() throws Exception {
        AuthRequest request = new AuthRequest("missing", "secret");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSucceedsOnlyForPersistedUserWithCorrectPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("demo", "demo123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthRequest goodRequest = new AuthRequest("demo", "demo123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodRequest)))
                .andExpect(status().isOk());

        AuthRequest badRequest = new AuthRequest("demo", "wrong-password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicLoginEndpointAcceptsRequestWithExpiredToken() throws Exception {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("test", "test");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Attempt login with an expired/garbage token in Authorization header
        // but with valid credentials in the body
        AuthRequest loginRequest = new AuthRequest("test", "test");
        mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Bearer expired.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    // Expect a JSON response with a non-empty data field (the token)
                    org.junit.jupiter.api.Assertions.assertTrue(contentAsString.contains("\"data\""), "Response should contain a data field with the token");
                });

        // Ensure that login with invalid password (no token) still returns 401
        AuthRequest badPasswordRequest = new AuthRequest("test", "wrong-password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }
}
