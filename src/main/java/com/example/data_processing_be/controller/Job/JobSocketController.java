package com.example.data_processing_be.controller.Job;

import com.example.data_processing_be.dto.Job.JobEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class JobSocketController {
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/jobs/{conversationId}")
  public void receiveJobEvent(@DestinationVariable String conversationId, JobEventResponse event) {
    messagingTemplate.convertAndSend("/topic/jobs/" + conversationId, event);
    System.out.println(conversationId + ": " + event);
  }
}
