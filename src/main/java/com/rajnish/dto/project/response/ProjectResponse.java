package com.rajnish.dto.project.response;

import com.rajnish.dto.auth.response.UserProfileResponse;
import com.rajnish.entity.User;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse owner
) {
}
