package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class JobController {

  private final ConversationService conversationService;

  @Value("${python.api.url:http://localhost:8000}")
  private String pythonApiUrl;

  @PostMapping
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("File không được để trống");
      }

      Conversation conversation = conversationService.createConversationForCurrentUser(
              file.getOriginalFilename()
      );

      Path uploadDir = Paths.get("../uploads");
      Files.createDirectories(uploadDir);

      String fileName = "input_" + conversation.getUser().getEmail() + "_" + conversation.getConversationId() + ".csv";

      Path filePath = uploadDir.resolve(fileName);
      file.transferTo(filePath.toFile());

      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("conversation_id", conversation.getConversationId().toString());
      requestBody.put("user_id", conversation.getUser().getUserId().toString());
      requestBody.put("file_path", filePath.toString().replace("\\", "/"));

      RestTemplate restTemplate = new RestTemplate();

      Object pythonResponse = restTemplate.postForObject(
              pythonApiUrl + "/jobs/start",
              requestBody,
              Object.class
      );

      return ResponseEntity.ok(pythonResponse);

    } catch (Exception e) {
      return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
    }
  }
}