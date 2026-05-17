package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.dto.Job.JobEventRequest;
import com.example.data_processing_be.service.Conversation.ConversationService;
import com.example.data_processing_be.service.Job.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;
    private final JobService jobService;

    @PostMapping("/event")
    public ResponseEntity<?> receiveEvent(@RequestBody JobEventRequest request) {
        System.out.println("Nhận event: " + request.getType() + " - " + request.getConversationId());
        System.out.println("Payload: " + request.getPayload());

        UUID conversationId = UUID.fromString(request.getConversationId());

        jobService.saveEvent(conversationId, request.getType(), request.getPayload());

        if ("JOB_DONE".equals(request.getType())) {
            if (request.getPayload() instanceof Map<?, ?> payload) {
                jobService.completeJob(conversationId, payload);
            }
            conversationService.updateConversationStatus(conversationId, "DONE");
        }

        if ("INTERRUPT".equals(request.getType())) {
            jobService.interruptJob(conversationId);
            conversationService.updateConversationStatus(conversationId, "WAITING_USER");
        }

        messagingTemplate.convertAndSend(
                "/topic/jobs/" + request.getConversationId(),
                request
        );

        return ResponseEntity.ok().build();
    }
}