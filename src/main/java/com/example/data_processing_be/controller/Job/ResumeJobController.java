package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.repository.UserRepository;
import com.example.data_processing_be.service.Conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class ResumeJobController {

  private final ConversationService conversationService;
  private final UserRepository userRepository;

  @Value("${python.api.url}")
  private String pythonApiUrl;

  @PostMapping("/{conversationId}/resume")
  public ResponseEntity<?> resumeJob(
          @PathVariable UUID conversationId,
          @RequestParam String answer
  ) {
    try {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      User user = userRepository.findByEmail(email)
              .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("conversation_id", conversationId.toString());
      requestBody.put("user_id", user.getId().toString());
      requestBody.put("answer", answer);

      RestTemplate restTemplate = new RestTemplate();
      restTemplate.postForObject(
              pythonApiUrl + "/jobs/resume",
              requestBody,
              Object.class
      );

      return ResponseEntity.ok().build();

    } catch (Exception e) {
      return ResponseEntity.status(500).body("Resume job thất bại: " + e.getMessage());
    }
  }
}