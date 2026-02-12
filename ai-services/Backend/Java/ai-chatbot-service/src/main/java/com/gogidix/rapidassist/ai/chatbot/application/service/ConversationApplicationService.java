package com.gogidix.rapidassist.ai.chatbot.application.service;

import com.gogidix.rapidassist.ai.chatbot.application.command.CreateConversationCommand;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationDto;
import com.gogidix.rapidassist.ai.chatbot.application.mapper.ConversationMapper;
import com.gogidix.rapidassist.ai.chatbot.domain.exception.ChatbotNotFoundException;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotRequest;
import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotRepository;
import com.gogidix.rapidassist.ai.chatbot.domain.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationApplicationService {
    private final ChatbotRepository repository;
    private final ConversationMapper mapper;

    public ConversationDto create(CreateConversationCommand command) {
        log.info("Creating {} for tenant: {}", "Conversation", TenantContext.getTenantId());
        ChatbotRequest entity = mapper.commandToEntity(command);
        ChatbotRequest saved = repository.save(entity);
        return mapper.entityToDto(saved);
    }

    public ConversationDto getById(String id) {
        String tenantId = TenantContext.getTenantId();
        return repository.findByIdAndTenantId(id, tenantId)
                .map(mapper::entityToDto)
                .orElseThrow(() -> new ChatbotNotFoundException(id, tenantId));
    }

    public List<ConversationDto> getAll() {
        return repository.findByTenantId(TenantContext.getTenantId()).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        repository.deleteByIdAndTenantId(id, tenantId);
    }
}
