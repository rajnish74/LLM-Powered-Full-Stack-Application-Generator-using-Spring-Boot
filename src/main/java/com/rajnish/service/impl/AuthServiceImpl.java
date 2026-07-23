package com.rajnish.service.impl;

import com.rajnish.common.exceptions.BadRequestException;
import com.rajnish.dto.auth.request.LoginRequest;
import com.rajnish.dto.auth.request.SignupRequest;
import com.rajnish.dto.auth.response.AuthResponse;
import com.rajnish.entity.User;
import com.rajnish.mapper.UserMapper;
import com.rajnish.repository.UserRepository;
import com.rajnish.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper  userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user->{
            throw new BadRequestException("User already exist with username: "+request.username());
        });


        User user=userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user=userRepository.save(user);

        //String token=authUtils.generateAccessToken(user);
        return new AuthResponse("dummy",userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
