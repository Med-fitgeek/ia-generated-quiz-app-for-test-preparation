package com.fitgeek.IATestPreparator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ObjectMapper objectMapper) {

        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .objectMapper(objectMapper)
                .outputType(new ParameterizedTypeReference<GeneratedQuizDto>() {})
                .maxRepeatAttempts(3)
                .build();

        return builder
                .defaultAdvisors(validationAdvisor)
                .build();
    }
}