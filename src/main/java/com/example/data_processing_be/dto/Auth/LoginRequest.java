package com.example.data_processing_be.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.io.*;

@Data
public class LoginRequest {
  @NotBlank(message = "Email không được để trống")
  @Email
  private String email;

  @NotBlank
  @Size(min = 6,message = "Mật khẩu phải chứa ít nhất 6 kí tự")
  private String password;
}
