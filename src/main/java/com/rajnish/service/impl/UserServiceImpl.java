package com.rajnish.service.impl;

import com.rajnish.dto.auth.response.UserProfileResponse;
import com.rajnish.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
