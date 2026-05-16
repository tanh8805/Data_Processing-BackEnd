package com.example.data_processing_be.dto.Job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeJobMessage {
  private String type;
  private UUID conversation_id;
  private UUID user_id;
  private String answer;
}