package com.example.data_processing_be.service.Job;

import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.entity.Job;
import com.example.data_processing_be.entity.JobEvent;
import com.example.data_processing_be.repository.JobEventRepository;
import com.example.data_processing_be.repository.JobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobEventRepository jobEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Job createJob(Conversation conversation, String originalFileName, String inputFilePath) {
        Job job = Job.builder()
                .conversation(conversation)
                .user(conversation.getUser())
                .originalFileName(originalFileName)
                .inputFilePath(inputFilePath)
                .status("PROCESSING")
                .build();
        return jobRepository.saveAndFlush(job);
    }

    public String getInputFilePath(UUID conversationId) {
        Job job = jobRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy job"));
        return job.getInputFilePath();
    }

    public void saveEvent(UUID conversationId, String eventType, Object payload) {
        Job job = jobRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy job"));
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            JobEvent event = JobEvent.builder()
                    .job(job)
                    .eventType(eventType)
                    .payload(payloadJson)
                    .build();
            jobEventRepository.save(event);
        } catch (Exception e) {
            System.out.println("Lỗi lưu event: " + e.getMessage());
        }
    }

    public void completeJob(UUID conversationId, Map<?, ?> payload) {
        Job job = jobRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy job"));

        int validCount = 0;
        int invalidCount = 0;

        if (payload.get("valid_rows") instanceof java.util.List<?> validRows) {
            validCount = validRows.size();
        }
        if (payload.get("invalid_rows") instanceof java.util.List<?> invalidRows) {
            invalidCount = invalidRows.size();
        }

        job.setStatus("DONE");
        job.setValidRowsCount(validCount);
        job.setInvalidRowsCount(invalidCount);
        job.setTotalRows(validCount + invalidCount);
        job.setOutputFilePath((String) payload.get("output_file_path"));
        jobRepository.save(job);
    }

    public void interruptJob(UUID conversationId) {
        Job job = jobRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy job"));
        job.setStatus("WAITING_USER");
        jobRepository.save(job);
    }

    public Job getJobForUser(UUID conversationId, UUID userId) {
        Job job = jobRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy job"));

        UUID ownerId = null;
        if (job.getUser() != null) {
            ownerId = job.getUser().getId();
        } else if (job.getConversation() != null && job.getConversation().getUser() != null) {
            ownerId = job.getConversation().getUser().getId();
        }

        if (ownerId == null) {
            throw new RuntimeException("Job chưa có chủ sở hữu");
        }
        if (!ownerId.equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập job này");
        }
        return job;
    }

    public void saveImputeChoice(UUID conversationId, UUID userId, String strategy, String prompt) {
        Job job = getJobForUser(conversationId, userId);
        if (strategy != null) {
            job.setImputeStrategy(strategy);
        }
        if (prompt != null && !prompt.isBlank()) {
            job.setImputePrompt(prompt);
        } else {
            job.setImputePrompt(null);
        }
        jobRepository.save(job);
    }
}