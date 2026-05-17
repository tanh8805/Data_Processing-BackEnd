package com.example.data_processing_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "fullname", length = 100)
  private String fullname;

  @Column(name = "email", length = 255, unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
  private String passwordHash;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
