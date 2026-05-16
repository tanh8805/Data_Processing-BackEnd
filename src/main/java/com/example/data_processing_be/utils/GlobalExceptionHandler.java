package com.example.data_processing_be.utils;

import com.example.data_processing_be.dto.ErrorMessage;
import com.example.data_processing_be.exception.EmailAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest().body(ErrorMessage.builder().message(ex.getMessage()).build());
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
    return ResponseEntity.badRequest().body(ErrorMessage.builder().message(ex.getMessage()).build());
  }

}
