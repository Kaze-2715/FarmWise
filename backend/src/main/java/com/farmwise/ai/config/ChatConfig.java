package com.farmwise.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient aiChatClient(
            ChatClient.Builder builder,
            ToolCallingManager toolCallingManager) {
        return builder.build();
    }
}
