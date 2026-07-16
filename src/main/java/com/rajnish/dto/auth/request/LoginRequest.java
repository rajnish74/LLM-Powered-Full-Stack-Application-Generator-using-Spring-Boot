package com.rajnish.dto.auth.request;

public record LoginRequest(
        String email,
        String password
) {
}
