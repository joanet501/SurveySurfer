package com.ahorahathayoga.SurveySurfer.dto;

import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import lombok.*;

public class AuthDtos {

    @Getter
    @Setter
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private UserRole role; // allow ADMIN/RESEARCHER for now
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Builder
    @Getter
    @Setter
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;
        private UserRole role;
    }
}