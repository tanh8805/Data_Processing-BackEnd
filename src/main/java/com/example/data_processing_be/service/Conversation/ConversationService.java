package com.example.data_processing_be.service;

import com.example.data_processing_be.dto.Conversation.ConversationResponse;
import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.repository.ConversationRepository;
import com.example.data_processing_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;

  public List<ConversationResponse> getMyConversations() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

    return conversationRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(conversation -> ConversationResponse.builder()
                    .conversation_id(conversation.getConversationId())
                    .title(conversation.getTitle())
                    .status(conversation.getStatus())
                    .created_at(conversation.getCreatedAt())
                    .build())
            .toList();
  }

  public Conversation createConversationForCurrentUser(String title) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

    Conversation conversation = Conversation.builder()
            .user(user)
            .title(title)
            .status("ACTIVE")
            .build();

    return conversationRepository.save(conversation);
  }
}