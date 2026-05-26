package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.Job;
import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.repository.UserRepository;
import com.example.data_processing_be.service.Job.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class DownloadController {

    private final JobService jobService;
    private final UserRepository userRepository;

    @GetMapping("/{conversationId}/download")
    public ResponseEntity<?> downloadOutputFile(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal Object ignoredPrincipal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            User currentUser = null;
            Object details = auth.getDetails();
            if (details instanceof User u) {
                currentUser = u;
            }

            if (currentUser == null) {
                String email = auth.getName();
                if (email == null || email.isBlank()) {
                    return ResponseEntity.status(401).body("Chưa đăng nhập");
                }
                currentUser = userRepository.findByEmail(email).orElse(null);
            }

            if (currentUser == null || currentUser.getId() == null) {
                return ResponseEntity.status(401).body("Không tìm thấy user");
            }

            Job job = jobService.getJobForUser(conversationId, currentUser.getId());

            if (!"DONE".equals(job.getStatus())) {
                return ResponseEntity.badRequest().body("Job chưa hoàn thành");
            }

            if (job.getOutputFilePath() == null) {
                return ResponseEntity.badRequest().body("File output chưa sẵn sàng");
            }

            Path filePath = Paths.get(job.getOutputFilePath());
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body("File không tồn tại trên server");
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileBytes);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi";
            if ("Không tìm thấy job".equals(msg)) {
                return ResponseEntity.status(404).body(msg);
            }
            if ("Không tìm thấy user".equals(msg)) {
                return ResponseEntity.status(401).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download thất bại: " + e.getMessage());
        }
    }
}