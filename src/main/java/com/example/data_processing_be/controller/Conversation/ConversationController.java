package com.example.data_processing_be.controller.Conversation;

import com.example.data_processing_be.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationService conversationService;

  @GetMapping
  public ResponseEntity<?> getMyConversations() {
    return ResponseEntity.ok(conversationService.getMyConversations());
  }
}