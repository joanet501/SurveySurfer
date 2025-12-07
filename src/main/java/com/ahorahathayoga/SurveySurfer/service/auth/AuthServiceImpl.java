package com.ahorahathayoga.SurveySurfer.service.auth;

import com.ahorahathayoga.SurveySurfer.dto.AuthDtos;
import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public User register(AuthDtos.RegisterRequest request) {
        // In production: validate, ensure uniqueness, limit roles, etc.

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.PUBLIC) // Default user role is PUBLIC (can change later with patch user role)
                .build();

        return userRepository.save(user);
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole());

        return AuthDtos.AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthDtos.AuthMeResponse me(){
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No content"));
        return AuthDtos.AuthMeResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getPrincipal().toString();
    }

}