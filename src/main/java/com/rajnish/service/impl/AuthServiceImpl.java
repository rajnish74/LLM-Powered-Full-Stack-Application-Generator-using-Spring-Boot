package com.rajnish.service.impl;

import com.rajnish.dto.auth.request.LoginRequest;
import com.rajnish.dto.auth.request.SignupRequest;
import com.rajnish.dto.auth.response.AuthResponse;
import com.rajnish.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
