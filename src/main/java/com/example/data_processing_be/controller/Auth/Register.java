package com.example.data_processing_be.controller.Auth;

import com.example.data_processing_be.dto.Auth.AuthResponse;
import com.example.data_processing_be.dto.Auth.RegisterRequest;
import com.example.data_processing_be.service.Auth.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Register {
  private final RegisterService registerService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    String fullName = request.getFullName();
    String email = request.getEmail();
    String password = request.getPassword();
    registerService.register(email, password, fullName);
    return ResponseEntity.ok().body(AuthResponse.builder().message("Tạo tài khoản thành công").build());
  }
}
