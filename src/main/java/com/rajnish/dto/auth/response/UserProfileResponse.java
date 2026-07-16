package com.rajnish.dto.auth.response;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String avatarUrl
) {
}
