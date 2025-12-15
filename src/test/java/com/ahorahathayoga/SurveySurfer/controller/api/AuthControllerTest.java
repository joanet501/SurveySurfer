package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.dto.AuthDtos;
import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private AuthDtos.RegisterRequest registerRequest;
    private AuthDtos.LoginRequest loginRequest;
    private User testUser;
    private AuthDtos.AuthResponse authResponse;
    private AuthDtos.AuthMeResponse meResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new AuthDtos.RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");

        loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Password123!");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        authResponse = AuthDtos.AuthResponse.builder()
                .token("test-jwt-token")
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.RESEARCHER)
                .build();

        meResponse = AuthDtos.AuthMeResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.RESEARCHER)
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Success")
    void testRegisterSuccess() throws Exception {
        when(authService.register(any(AuthDtos.RegisterRequest.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Username Already Exists")
    void testRegisterUsernameExists() throws Exception {
        when(authService.register(any(AuthDtos.RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Invalid Email")
    void testRegisterInvalidEmail() throws Exception {
        registerRequest.setEmail("invalid-email");
        when(authService.register(any(AuthDtos.RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email format"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Success")
    void testLoginSuccess() throws Exception {
        when(authService.login(any(AuthDtos.LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Invalid Credentials")
    void testLoginInvalidCredentials() throws Exception {
        when(authService.login(any(AuthDtos.LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Missing Username")
    void testLoginMissingUsername() throws Exception {
        loginRequest.setUsername(null);
        when(authService.login(any(AuthDtos.LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Username is required"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/auth/me - Success")
    void testGetCurrentUserSuccess() throws Exception {
        AuthDtos.AuthMeResponse meResponse = AuthDtos.AuthMeResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.RESEARCHER)
                .build();

        when(authService.me()).thenReturn(meResponse);

        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /api/auth/me - Unauthorized")
    void testGetCurrentUserUnauthorized() throws Exception {
        when(authService.me()).thenThrow(new IllegalArgumentException("User not authenticated"));

        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
