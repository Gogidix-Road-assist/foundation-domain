package com.gogidix.rapidassist.ai.chatbot.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationCommand {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String status;
    private Map<String, Object> metadata;
}
