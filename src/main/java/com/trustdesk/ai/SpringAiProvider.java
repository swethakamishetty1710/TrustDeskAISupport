package com.trustdesk.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class SpringAiProvider implements AiProvider {

    private final ChatClient chatClient;

    public SpringAiProvider(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generate(String prompt) {
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}