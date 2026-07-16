package com.rajnish.dto.auth.response;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
