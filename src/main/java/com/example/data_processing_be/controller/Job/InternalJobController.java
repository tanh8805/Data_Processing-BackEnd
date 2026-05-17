package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.dto.Job.JobEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/event")
    public ResponseEntity<?> receiveEvent(@RequestBody JobEventRequest request) {
        messagingTemplate.convertAndSend(
                "/topic/jobs/" + request.getConversationId(),
                request
        );
        return ResponseEntity.ok().build();
    }
}