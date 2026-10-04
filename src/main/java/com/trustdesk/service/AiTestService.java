package com.trustdesk.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiTestService {

    private final ChatClient chatClient;

    public AiTestService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String ask(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}