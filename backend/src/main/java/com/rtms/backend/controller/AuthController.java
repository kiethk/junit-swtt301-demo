package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.LoginRequest;
import com.rtms.backend.dto.LoginResponse;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.AuthService;
import com.rtms.backend.service.AuthService.LoginResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResult result = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("jwt_token", result.token())
                .httpOnly(true)
                .secure(false) // false because localhost (http, no https)
                .path("/")
                .maxAge(jwtExpirationMs / 1000) // Convert milliseconds to seconds
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ApiResponse.success(result.loginResponse());
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ApiResponse.success("Logged out successfully");
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse> getMe() {
        // Lấy thông tin currentUser từ SecurityContext
        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return ApiResponse.success(authService.getMe(currentUser.getUserId()));
    }

}
