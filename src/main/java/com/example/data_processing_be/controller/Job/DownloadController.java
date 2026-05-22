package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.entity.Job;
import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.service.Job.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{conversationId}/download")
    public ResponseEntity<?> downloadOutputFile(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
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

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download thất bại: " + e.getMessage());
        }
    }
}