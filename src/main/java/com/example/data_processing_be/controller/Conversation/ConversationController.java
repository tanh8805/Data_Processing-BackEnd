package com.example.data_processing_be.controller.Conversation;

import org.springframework.security.access.AccessDeniedException;
import com.example.data_processing_be.dto.Conversation.UpdateConversationTitleRequest;
import com.example.data_processing_be.service.Conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationService conversationService;

  @GetMapping
  public ResponseEntity<?> getMyConversations() {
    return ResponseEntity.ok(conversationService.getMyConversations());
  }

  @PutMapping("/{conversationId}/title")
  public ResponseEntity<?> updateTitle(
      @PathVariable UUID conversationId,
      @RequestBody UpdateConversationTitleRequest request) {
    try {
      return ResponseEntity.ok(conversationService.updateTitle(conversationId, request.getTitle()));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(403).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("/{conversationId}")
  public ResponseEntity<?> deleteConversation(@PathVariable UUID conversationId) {
    try {
      conversationService.deleteConversation(conversationId);
      return ResponseEntity.noContent().build();
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(403).body(e.getMessage());
    } catch (RuntimeException e) {
      String msg = e.getMessage() != null ? e.getMessage() : "Lỗi";
      if ("Không tìm thấy conversation".equals(msg)) {
        return ResponseEntity.status(404).body(msg);
      }
      return ResponseEntity.badRequest().body(msg);
    }
  }
}