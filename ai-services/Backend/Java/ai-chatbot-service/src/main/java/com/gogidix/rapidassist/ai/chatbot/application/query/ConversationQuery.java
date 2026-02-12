package com.gogidix.rapidassist.ai.chatbot.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ConversationQuery {
    private String name;
    private String status;
    private String searchQuery;
    private int page = 0;
    private int size = 20;
}
