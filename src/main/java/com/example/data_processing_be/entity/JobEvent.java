package com.example.data_processing_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Job job;

  @Column(name = "event_type", nullable = false, length = 50)
  private String eventType;

  @Column(name = "payload", columnDefinition = "jsonb")
  private String payload;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}