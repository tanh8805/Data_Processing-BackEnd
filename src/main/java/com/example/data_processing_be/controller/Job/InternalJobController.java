package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.dto.Job.JobEventRequest;
import com.example.data_processing_be.service.Conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    @PostMapping("/event")
    public ResponseEntity<?> receiveEvent(@RequestBody JobEventRequest request) {
        System.out.println("Nhận event: " + request.getType() + " - " + request.getConversationId());
        System.out.println("Payload: " + request.getPayload());

        if ("JOB_DONE".equals(request.getType())) {
            conversationService.updateConversationStatus(
                UUID.fromString(request.getConversationId()), "DONE"
            );
        }
        if ("INTERRUPT".equals(request.getType())) {
            conversationService.updateConversationStatus(
                UUID.fromString(request.getConversationId()), "WAITING_USER"
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/jobs/" + request.getConversationId(),
                request
        );

        return ResponseEntity.ok().build();
    }
}