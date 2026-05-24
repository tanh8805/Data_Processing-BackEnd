package com.example.data_processing_be.config.jwt;

import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository; 

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            if (jwtService.isTokenExpired(token)) {
                response.setContentType("application/json");
                response.setStatus(401);
                response.getWriter().write("{\"message\":\"Token hết hạn\"}");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtService.extractEmail(token);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User không tồn tại"));

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token không hợp lệ!\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}