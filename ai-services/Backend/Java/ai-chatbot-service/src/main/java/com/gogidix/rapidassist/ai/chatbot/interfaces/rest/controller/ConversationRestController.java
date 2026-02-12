package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.chatbot.application.command.CreateConversationCommand;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationDto;
import com.gogidix.rapidassist.ai.chatbot.application.service.ConversationApplicationService;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.ConversationRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response.ConversationResponse;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.mapper.ConversationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "Conversation management APIs")
public class ConversationRestController {
    private final ConversationApplicationService service;
    private final ConversationRestMapper restMapper;

    @PostMapping
    @Operation(summary = "Create new Conversation")
    public ResponseEntity<ConversationResponse> create(@Valid @RequestBody ConversationRequest request) {
        CreateConversationCommand command = restMapper.requestToCommand(request);
        ConversationDto dto = service.create(command);
        return ResponseEntity.status(201).body(restMapper.dtoToResponse(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Conversation by ID")
    public ResponseEntity<ConversationResponse> getById(@PathVariable String id) {
        ConversationDto dto = service.getById(id);
        return ResponseEntity.ok(restMapper.dtoToResponse(dto));
    }

    @GetMapping
    @Operation(summary = "Get all Conversation")
    public ResponseEntity<List<ConversationResponse>> getAll() {
        List<ConversationResponse> responses = service.getAll().stream()
                .map(restMapper::dtoToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Conversation")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
