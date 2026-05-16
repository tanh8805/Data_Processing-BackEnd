package com.example.data_processing_be.repository;

import com.example.data_processing_be.entity.Conversation;
import com.example.data_processing_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
  List<Conversation> findByUserOrderByCreatedAtDesc(User user);
}