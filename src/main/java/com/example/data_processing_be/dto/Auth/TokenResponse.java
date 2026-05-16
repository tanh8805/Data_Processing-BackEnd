package com.example.data_processing_be.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.io.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
  String message;
  String token;
}
