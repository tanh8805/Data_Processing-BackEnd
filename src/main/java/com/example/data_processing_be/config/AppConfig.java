package com.example.data_processing_be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import com.example.data_processing_be.repository.UserRepository;
import com.example.data_processing_be.repository.ConversationRepository;
import com.example.data_processing_be.repository.JobRepository;
import com.example.data_processing_be.repository.JobEventRepository;

@SpringBootTest
class DataProcessingBeApplicationTests {

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ConversationRepository conversationRepository;

  @MockBean
  private JobRepository jobRepository;

  @MockBean
  private JobEventRepository jobEventRepository;

  @Test
  void contextLoads() {
  }
}