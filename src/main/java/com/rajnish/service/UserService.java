package com.rajnish.service;

import com.rajnish.dto.auth.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
