package com.example.data_processing_be.controller.Upload;

import com.example.data_processing_be.config.rabbitmq.RabbitMQConfig;
import com.example.data_processing_be.dto.Job.JobMessage;
import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

  private final RabbitTemplate rabbitTemplate;
  private final ConversationService conversationService;

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

      String fileName = "input_" + conversation.getUser().getEmail() + conversation.getConversationId() + ".csv";
      Path filePath = uploadDir.resolve(fileName);

      file.transferTo(filePath.toFile());

      JobMessage message = JobMessage.builder()
              .type("START_JOB")
              .conversation_id(conversation.getConversationId())
              .user_id(UUID.fromString(conversation.getUser().getUserId()))
              .file_path(filePath.toString().replace("\\", "/"))
              .build();

      rabbitTemplate.convertAndSend(RabbitMQConfig.JOB_QUEUE, message);

      return ResponseEntity.ok(message);

    } catch (Exception e) {
      return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
    }
  }
}