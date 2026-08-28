package com.jobtrack.applications;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class ApplicationReminderTest {

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
    void testFollowUpDateAndOverdueLogic() throws Exception {
        String token = registerAndLoginToken();

        // Create an application with a future follow-up date (not overdue)
        ApplicationRequest request = new ApplicationRequest(
                "Test Corp",
                "Test Position",
                "Test Source",
                LocalDate.of(2026, 7, 28),
                "Applied",
                "Test notes",
                LocalDate.of(2026, 9, 1) // Future date
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

        // Check that the application is not overdue
        MvcResult getResult = mockMvc.perform(get("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode applicationData = objectMapper.readTree(getResult.getResponse().getContentAsString()).path("data");
        assertFalse(applicationData.path("overdue").asBoolean());

        // Update the application with a past follow-up date (overdue)
        ApplicationRequest updateRequest = new ApplicationRequest(
                "Test Corp",
                "Test Position",
                "Test Source",
                LocalDate.of(2026, 7, 28),
                "Applied",
                "Updated test notes",
                LocalDate.of(2026, 8, 1) // Past date
        );

        mockMvc.perform(put("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Check that the application is now overdue
        getResult = mockMvc.perform(get("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        applicationData = objectMapper.readTree(getResult.getResponse().getContentAsString()).path("data");
        assertTrue(applicationData.path("overdue").asBoolean());
    }

    @Test
    void testNotesField() throws Exception {
        String token = registerAndLoginToken();

        // Create an application with notes
        ApplicationRequest request = new ApplicationRequest(
                "Notes Corp",
                "Notes Position",
                "Notes Source",
                LocalDate.of(2026, 7, 28),
                "Applied",
                "These are test notes",
                LocalDate.of(2026, 8, 10)
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
        assertEquals("These are test notes", created.path("notes").asText());

        // Update the notes
        ApplicationRequest updateRequest = new ApplicationRequest(
                "Notes Corp",
                "Notes Position",
                "Notes Source",
                LocalDate.of(2026, 7, 28),
                "Applied",
                "Updated test notes",
                LocalDate.of(2026, 8, 10)
        );

        mockMvc.perform(put("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Check that the notes were updated
        MvcResult getResult = mockMvc.perform(get("/api/applications/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode applicationData = objectMapper.readTree(getResult.getResponse().getContentAsString()).path("data");
        assertEquals("Updated test notes", applicationData.path("notes").asText());
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