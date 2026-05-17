package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.dto.Job.JobEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/event")
    public ResponseEntity<?> receiveEvent(@RequestBody JobEventRequest request) {
        System.out.println("Nhận event: " + request.getType() + " - " + request.getConversationId());
        System.out.println("Payload: " + request.getPayload());

        messagingTemplate.convertAndSend(
                "/topic/jobs/" + request.getConversationId(),
                request
        );

        return ResponseEntity.ok().build();
    }
}