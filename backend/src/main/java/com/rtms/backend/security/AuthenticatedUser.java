package com.rtms.backend.security;

public class AuthenticatedUser {
    private final Long userId;
    private final String email;
    private final String role;

    public AuthenticatedUser(Long userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}