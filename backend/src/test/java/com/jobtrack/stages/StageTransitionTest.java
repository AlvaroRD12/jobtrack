package com.jobtrack.stages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrack.applications.ApplicationRequest;
import com.jobtrack.auth.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class StageTransitionTest {

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
    void userCanMoveApplicationStage() throws Exception {
        String token = registerAndLoginToken("owner");

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
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

        mockMvc.perform(put("/api/applications/{id}/stage", applicationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\":\"Interview\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stage").value("Interview"));
    }

    @Test
    void unknownStageReturns400() throws Exception {
        String token = registerAndLoginToken("owner");

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
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

        mockMvc.perform(put("/api/applications/{id}/stage", applicationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\":\"Mystery\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown stage: Mystery"));
    }

    @Test
    void terminalStageIsDerivedFromStage() throws Exception {
        String token = registerAndLoginToken("owner");

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
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

        mockMvc.perform(put("/api/applications/{id}/stage", applicationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\":\"Offer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stage").value("Offer"));
    }

    @Test
    void userCannotMoveAnotherUsersApplicationStage() throws Exception {
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

        mockMvc.perform(put("/api/applications/{id}/stage", applicationId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\":\"Interview\"}"))
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
}
