package com.example.data_processing_be.dto.Job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobEventRequest {

    private String type;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("user_id")
    private String userId;

    private Object payload;
}