package com.rajnish.dto.auth.request;

public record SignupRequest(
        String email,
        String name,
        String password
) {
}
