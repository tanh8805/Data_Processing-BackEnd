package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.service.Conversation.ConversationService;
import com.example.data_processing_be.service.Job.JobTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class JobController {

    private final ConversationService conversationService;
    private final JobTriggerService jobTriggerService;

    @Value("${upload.dir}")
    private String uploadDirPath;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File không được để trống");
            }

            Conversation conversation = conversationService.createConversationForCurrentUser(
                    file.getOriginalFilename());

            log.info("[UPLOAD] Conversation created: id={}", conversation.getId());

            Path uploadDir = Paths.get(uploadDirPath).toAbsolutePath();
            Files.createDirectories(uploadDir);

            String fileName = "input_" + conversation.getUser().getEmail() + "_" + conversation.getId() + ".csv";
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);
            String filePathStr = filePath.toString().replace("\\", "/");

            jobTriggerService.createJob(conversation, file.getOriginalFilename(), filePathStr);

            log.info("[UPLOAD] Job created for conversation: id={}", conversation.getId());

            jobTriggerService.triggerPython(conversation, filePathStr);

            log.info("[UPLOAD] Python triggered: conversation_id={}", conversation.getId());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("[UPLOAD] Thất bại: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
        }
    }
}