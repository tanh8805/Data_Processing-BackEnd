package com.example.data_processing_be.dto.Conversation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConversationResponse {
  private UUID conversation_id;
  private String title;
  private String status;
  private LocalDateTime created_at;
}