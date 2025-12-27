package com.ahorahathayoga.SurveySurfer.service.auth;

import com.ahorahathayoga.SurveySurfer.dto.AuthDtos;
import com.ahorahathayoga.SurveySurfer.model.User;

public interface AuthService {

    User register(AuthDtos.RegisterRequest request);

    AuthDtos.AuthResponse login(AuthDtos.LoginRequest request);

    AuthDtos.AuthMeResponse me();


}