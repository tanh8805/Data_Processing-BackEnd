package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.service.Conversation.ConversationService;
import com.example.data_processing_be.service.Job.JobService;
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

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class JobController {

    private final ConversationService conversationService;
    private final JobService jobService;
    private final JobTriggerService jobTriggerService; // ← tách riêng

    @Value("${python.api.url}")
    private String pythonApiUrl;

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

            Path uploadDir = Paths.get(uploadDirPath).toAbsolutePath();
            Files.createDirectories(uploadDir);

            String fileName = "input_" + conversation.getUser().getEmail() + "_" + conversation.getId() + ".csv";
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);

            String filePathStr = filePath.toString().replace("\\", "/");

            // ✅ Save job và commit xong mới gọi Python
            jobTriggerService.createJobAndTriggerPython(
                    conversation,
                    file.getOriginalFilename(),
                    filePathStr);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
        }
    }
}