package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.service.survey.SurveyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicSurveyController.class)
@DisplayName("PublicSurveyController Tests")
class PublicSurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SurveyService surveyService;

    private Survey publicSurvey;

    @BeforeEach
    void setUp() {
        publicSurvey = new Survey();
        publicSurvey.setId(1L);
        publicSurvey.setTitle("Public Survey");
        publicSurvey.setSlug("public-survey");
        publicSurvey.setDescription("Public Description");
        publicSurvey.setIsPublic(true);
    }

    @Test
    @DisplayName("Should get public survey by slug")
    void shouldGetPublicSurveyBySlug() throws Exception {
        when(surveyService.findWithQuestionsBySlug(anyString())).thenReturn(Optional.of(publicSurvey));

        mockMvc.perform(get("/api/public/surveys/{slug}", "public-survey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.slug").value("public-survey"))
                .andExpect(jsonPath("$.title").value("Public Survey"))
                .andExpect(jsonPath("$.description").value("Public Description"));
    }

    @Test
    @DisplayName("Should return 500 when survey not found")
    void shouldReturn500WhenSurveyNotFound() throws Exception {
        when(surveyService.findWithQuestionsBySlug(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/public/surveys/{slug}", "non-existent"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Should save draft submission")
    void shouldSaveDraftSubmission() throws Exception {
        String draftRequest = """
                {
                    "submissionId": "sub_123",
                    "responses": []
                }
                """;

        mockMvc.perform(post("/api/public/surveys/{id}/draft", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(draftRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").exists())
                .andExpect(jsonPath("$.savedAt").exists());
    }

    @Test
    @DisplayName("Should submit survey")
    void shouldSubmitSurvey() throws Exception {
        String submitRequest = """
                {
                    "responses": []
                }
                """;

        mockMvc.perform(post("/api/public/surveys/{id}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.submittedAt").exists());
    }
}
