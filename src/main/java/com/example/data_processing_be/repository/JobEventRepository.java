package com.example.data_processing_be.repository;

import com.example.data_processing_be.entity.JobEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobEventRepository extends JpaRepository<JobEvent, UUID> {

    @Query("SELECT e FROM JobEvent e WHERE e.job.conversation.id = :conversationId ORDER BY e.createdAt ASC")
    List<JobEvent> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") UUID conversationId);
}