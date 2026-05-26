package com.example.data_processing_be.service.Job;

import com.example.data_processing_be.entity.Conversation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobTriggerService {

    private final JobService jobService;
    private final RestTemplate restTemplate;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createJob(Conversation conversation, String originalFileName, String filePathStr) {
        jobService.createJob(conversation, originalFileName, filePathStr);
        log.info("[JOB-TRIGGER] Job saved: conversation_id={}", conversation.getId());
    }

    public void triggerPython(Conversation conversation, String filePathStr) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("conversation_id", conversation.getId().toString());
        requestBody.put("user_id", conversation.getUser().getId().toString());
        requestBody.put("file_path", filePathStr);

        log.info("[PYTHON] Gửi sang Python: conversation_id={}, file_path={}", conversation.getId(), filePathStr);

        try {
            restTemplate.postForObject(pythonApiUrl + "/jobs/start", requestBody, Object.class);
        } catch (Exception e) {
            log.error("[PYTHON] Gọi Python thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể kết nối Python service: " + e.getMessage());
        }
    }
}