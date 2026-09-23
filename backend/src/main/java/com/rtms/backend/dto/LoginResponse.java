package com.rtms.backend.dto;

public class LoginResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private Object profile;

    // Constructor cho login (không cần profile)
    public LoginResponse(Long userId, String fullName, String email, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Constructor cho /me (có profile)
    public LoginResponse(Long userId, String fullName, String email, String role, Object profile) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.profile = profile;
    }

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Object getProfile() { return profile; }
}
