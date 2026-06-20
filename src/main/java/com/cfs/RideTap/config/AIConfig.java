package com.cfs.RideTap.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Bean
    public ChatClient chatClient(GoogleGenAiChatModel googleGenAiChatModel) {
        return ChatClient.create(googleGenAiChatModel);
    }
}
