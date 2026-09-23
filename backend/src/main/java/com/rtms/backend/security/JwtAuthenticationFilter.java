package com.rtms.backend.security;

import com.rtms.backend.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RoleRepository roleRepository) {
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            Claims claims = jwtUtil.extractClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Number.class).longValue();

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, email, role);

            List<GrantedAuthority> authorities = roleRepository.findByName(role)
                    .map(r -> r.getPermissions().stream()
                            .map(p -> (GrantedAuthority) new SimpleGrantedAuthority(p.getCode()))
                            .collect(Collectors.toList()))
                    .orElse(List.of());

            var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}