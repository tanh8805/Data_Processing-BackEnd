package com.example.data_processing_be.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Lỗi format email")
  private String email;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 kí tự")
  private String password;

  @NotBlank(message = "Tên không được để trống")
  private String fullName;
}
