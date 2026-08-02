package com.jobtrack.applications;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrack.auth.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationApiTest {

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
    void createEditArchiveAndDeleteApplicationFlow() throws Exception {
        String token = registerAndLoginToken();

        ApplicationRequest request = new ApplicationRequest(
                "Acme Corp",
                "Backend Engineer",
                "LinkedIn",
                LocalDate.of(2026, 7, 28),
                "Applied",
                "Initial outreach sent",
                LocalDate.of(2026, 8, 4)
        );

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString()).path("data");
        long id = created.path("id").asLong();
        assertTrue(id > 0);
        assertEquals("Acme Corp", created.path("company").asText());

        ApplicationRequest updateRequest = new ApplicationRequest(
                "Acme Corp",
                "Senior Backend Engineer",
                "Referral",
                LocalDate.of(2026, 7, 28),
                "Interview",
                "Interview scheduled",
                LocalDate.of(2026, 8, 10)
        );

        MvcResult updateResult = mockMvc.perform(put("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updated = objectMapper.readTree(updateResult.getResponse().getContentAsString()).path("data");
        assertEquals("Senior Backend Engineer", updated.path("position").asText());
        assertEquals("Interview", updated.path("stage").asText());

        mockMvc.perform(patch("/api/applications/{id}/archive", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"archived\":true}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedGetApplicationsReturns401() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void userCannotArchiveAnotherUsersApplication() throws Exception {
        String ownerToken = registerAndLoginToken("owner");
        String otherToken = registerAndLoginToken("other");

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ApplicationRequest(
                                "Acme Corp",
                                "Backend Engineer",
                                "LinkedIn",
                                LocalDate.of(2026, 7, 28),
                                "Applied",
                                "Initial outreach sent",
                                LocalDate.of(2026, 8, 4)
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString()).path("data");
        long applicationId = created.path("id").asLong();

        mockMvc.perform(patch("/api/applications/{id}/archive", applicationId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"archived\":true}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have access to this application"));
    }

    @Test
    void userCannotUpdateOrDeleteAnotherUsersApplication() throws Exception {
        String ownerToken = registerAndLoginToken("owner");
        String otherToken = registerAndLoginToken("other");

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ApplicationRequest(
                                "Acme Corp",
                                "Backend Engineer",
                                "LinkedIn",
                                LocalDate.of(2026, 7, 28),
                                "Applied",
                                "Initial outreach sent",
                                LocalDate.of(2026, 8, 4)
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString()).path("data");
        long applicationId = created.path("id").asLong();

        mockMvc.perform(put("/api/applications/{id}", applicationId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ApplicationRequest(
                                "Acme Corp",
                                "Senior Backend Engineer",
                                "Referral",
                                LocalDate.of(2026, 7, 28),
                                "Interview",
                                "Interview scheduled",
                                LocalDate.of(2026, 8, 10)
                        ))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have access to this application"));

        mockMvc.perform(delete("/api/applications/{id}", applicationId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have access to this application"));
    }

    private String registerAndLoginToken(String username) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"demo123\"}"))
                .andExpect(status().isCreated());

        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginBody).path("data").asText();
    }

    private String registerAndLoginToken() throws Exception {
        return registerAndLoginToken("demo");
    }
}
