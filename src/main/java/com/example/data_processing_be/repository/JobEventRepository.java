package com.example.data_processing_be.repository;

import com.example.data_processing_be.entity.JobEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobEventRepository extends JpaRepository<JobEvent, UUID> {

  List<JobEvent> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);
}