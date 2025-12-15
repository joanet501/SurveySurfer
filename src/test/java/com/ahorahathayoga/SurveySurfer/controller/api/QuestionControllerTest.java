package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.dto.survey.*;
import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.service.survey.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
@DisplayName("QuestionController Tests")
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private UserRepository userRepository;

    private User testUser;
    private QuestionDto questionDto;
    private CreateQuestionRequest createRequest;
    private UpdateQuestionRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        questionDto = QuestionDto.builder()
                .id("1")
                .text("Test Question")
                .type("RADIO")
                .required(true)
                .order(1)
                .build();

        createRequest = CreateQuestionRequest.builder()
                .text("New Question")
                .type("RADIO")
                .required(true)
                .build();

        updateRequest = UpdateQuestionRequest.builder()
                .text("Updated Question")
                .required(false)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("POST /api/surveys/{surveyId}/questions - Create Question Success")
    @WithMockUser(username = "testuser")
    void testCreateQuestionSuccess() throws Exception {
        when(questionService.createQuestion(eq(1L), any(CreateQuestionRequest.class), any(User.class)))
                .thenReturn(questionDto);

        mockMvc.perform(post("/api/surveys/1/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.text").value("Test Question"))
                .andExpect(jsonPath("$.type").value("RADIO"));
    }

    @Test
    @DisplayName("PATCH /api/surveys/{surveyId}/questions/{questionId} - Update Question Success")
    @WithMockUser(username = "testuser")
    void testUpdateQuestionSuccess() throws Exception {
        questionDto.setText("Updated Question");
        questionDto.setRequired(false);

        when(questionService.updateQuestion(eq(1L), eq(1L), any(UpdateQuestionRequest.class), any(User.class)))
                .thenReturn(questionDto);

        mockMvc.perform(patch("/api/surveys/1/questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated Question"))
                .andExpect(jsonPath("$.required").value(false));
    }

    @Test
    @DisplayName("DELETE /api/surveys/{surveyId}/questions/{questionId} - Delete Question Success")
    @WithMockUser(username = "testuser")
    void testDeleteQuestionSuccess() throws Exception {
        mockMvc.perform(delete("/api/surveys/1/questions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/surveys/{surveyId}/questions/reorder - Reorder Questions Success")
    @WithMockUser(username = "testuser")
    void testReorderQuestionsSuccess() throws Exception {
        QuestionDto question1 = QuestionDto.builder().id("1").order(1).build();
        QuestionDto question2 = QuestionDto.builder().id("2").order(2).build();
        List<QuestionDto> reorderedQuestions = Arrays.asList(question1, question2);

        ReorderQuestionsRequest reorderRequest = new ReorderQuestionsRequest();
        reorderRequest.setOrder(Arrays.asList("1", "2"));

        when(questionService.reorderQuestions(eq(1L), any(ReorderQuestionsRequest.class), any(User.class)))
                .thenReturn(reorderedQuestions);

        mockMvc.perform(post("/api/surveys/1/questions/reorder")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions[0].id").value("1"))
                .andExpect(jsonPath("$.questions[1].id").value("2"));
    }
}
