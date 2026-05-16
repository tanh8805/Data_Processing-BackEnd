package com.example.data_processing_be.controller.Auth;

import com.example.data_processing_be.config.jwt.JwtService;
import com.example.data_processing_be.dto.Auth.LoginRequest;
import com.example.data_processing_be.dto.Auth.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Login {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtService.generateToken(authentication);
    return ResponseEntity.status(200).body(TokenResponse.builder().message("Đăng nhập thành công").token(token).build());
  }
}
