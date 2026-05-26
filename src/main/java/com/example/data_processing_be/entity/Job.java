package com.example.data_processing_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @Column(name = "original_file_name")
  private String originalFileName;

  @Column(name = "input_file_path", nullable = false, columnDefinition = "TEXT")
  private String inputFilePath;

  @Column(name = "output_file_path", columnDefinition = "TEXT")
  private String outputFilePath;

  @Column(name = "status", nullable = false, length = 50)
  private String status;

  @Column(name = "impute_strategy", length = 50)
  private String imputeStrategy;

  @Column(name = "impute_prompt", columnDefinition = "TEXT")
  private String imputePrompt;

  @Column(name = "total_rows")
  private Integer totalRows;

  @Column(name = "valid_rows_count")
  private Integer validRowsCount;

  @Column(name = "invalid_rows_count")
  private Integer invalidRowsCount;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}