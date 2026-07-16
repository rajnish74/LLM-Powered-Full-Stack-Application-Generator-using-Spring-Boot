package com.rajnish.service;

import com.rajnish.dto.auth.request.LoginRequest;
import com.rajnish.dto.auth.request.SignupRequest;
import com.rajnish.dto.auth.response.AuthResponse;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
