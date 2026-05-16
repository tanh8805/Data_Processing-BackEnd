package com.example.data_processing_be.config.rabbitmq;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String JOB_QUEUE = "job_queue";

  @Bean
  public Queue jobQueue() {
    return new Queue(JOB_QUEUE, true);
  }
}