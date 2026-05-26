package com.example.data_processing_be.service.Job;

import com.example.data_processing_be.entity.Conversation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobTriggerService {

    private final JobService jobService;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Transactional
    public void createJobAndTriggerPython(
            Conversation conversation,
            String originalFileName,
            String filePathStr) {
        // ✅ Bước 1: Save job, transaction commit khi method kết thúc
        jobService.createJob(conversation, originalFileName, filePathStr);
    }

    // ✅ Bước 2: Gọi Python SAU KHI transaction đã commit
    public void triggerPython(Conversation conversation, String filePathStr) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("conversation_id", conversation.getId().toString());
        requestBody.put("user_id", conversation.getUser().getId().toString());
        requestBody.put("file_path", filePathStr);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(
                pythonApiUrl + "/jobs/start",
                requestBody,
                Object.class);
    }
}