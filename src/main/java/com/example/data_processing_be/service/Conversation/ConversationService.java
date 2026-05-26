package com.example.data_processing_be.service.Conversation;

import com.example.data_processing_be.dto.Conversation.ConversationResponse;
import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.entity.User;
import com.example.data_processing_be.repository.ConversationRepository;
import com.example.data_processing_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
                                                .conversation_id(conversation.getId())
                                                .title(conversation.getTitle())
                                                .status(conversation.getStatus())
                                                .created_at(conversation.getCreatedAt())
                                                .build())
                                .toList();
        }

        public ConversationResponse updateTitle(UUID conversationId, String title) {
                if (title == null || title.trim().isEmpty()) {
                        throw new RuntimeException("Tên cuộc trò chuyện không được để trống");
                }

                User user = getCurrentUser();
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));
                if (!conversation.getUser().getId().equals(user.getId())) {
                        throw new AccessDeniedException("Bạn không có quyền cập nhật conversation này");
                }

                conversation.setTitle(title.trim());
                Conversation saved = conversationRepository.save(conversation);

                return ConversationResponse.builder()
                                .conversation_id(saved.getId())
                                .title(saved.getTitle())
                                .status(saved.getStatus())
                                .created_at(saved.getCreatedAt())
                                .build();
        }

        public void deleteConversation(UUID conversationId) {
                User user = getCurrentUser();
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));
                if (!conversation.getUser().getId().equals(user.getId())) {
                        throw new AccessDeniedException("Bạn không có quyền xoá conversation này");
                }
                conversationRepository.delete(conversation);
        }

        private User getCurrentUser() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        }

        public Conversation createConversationForCurrentUser(String title) {
                User user = getCurrentUser();

                Conversation conversation = Conversation.builder()
                                .user(user)
                                .title(title)
                                .status("ACTIVE")
                                .build();

                return conversationRepository.save(conversation);
        }

        public Conversation updateConversationStatus(UUID conversationId, String status) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));

                conversation.setStatus(status);

                return conversationRepository.save(conversation);
        }
}