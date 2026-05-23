package com.example.data_processing_be.repository;

import com.example.data_processing_be.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    @Query("SELECT j FROM Job j WHERE j.conversation.id = :conversationId")
    Optional<Job> findByConversationId(@Param("conversationId") UUID conversationId);
}