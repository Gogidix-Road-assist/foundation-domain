package com.gogidix.rapidassist.ai.chatbot.application.service;

import com.gogidix.rapidassist.ai.chatbot.application.command.*;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotSessionDto;
import com.gogidix.rapidassist.ai.chatbot.application.mapper.ChatbotSessionMapper;
import com.gogidix.rapidassist.ai.chatbot.domain.event.*;
import com.gogidix.rapidassist.ai.chatbot.domain.exception.ChatbotSessionNotFoundException;
import com.gogidix.rapidassist.ai.chatbot.domain.exception.InvalidChatbotSessionException;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageType;
import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotContextRepositoryPort;
import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotMessageRepositoryPort;
import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotSessionRepositoryPort;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.event.EventPublisher;
import com.gogidix.rapidassist.ai.chatbot.application.query.GetChatbotSessionBySessionIdQuery;
import com.gogidix.rapidassist.ai.chatbot.application.query.GetChatbotSessionQuery;
import com.gogidix.rapidassist.ai.chatbot.application.query.GetUserSessionsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ChatbotSessionApplicationService.
 * Tests all use cases, error handling, and integration with repositories.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChatbotSessionApplicationService Tests")
class ChatbotSessionApplicationServiceTest {

    @Mock
    private ChatbotSessionRepositoryPort sessionRepository;

    @Mock
    private ChatbotMessageRepositoryPort messageRepository;

    @Mock
    private ChatbotContextRepositoryPort contextRepository;

    @Mock
    private ChatbotSessionMapper mapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ChatbotSessionApplicationService service;

    private String tenantId = "tenant-123";
    private String userId = "user-456";
    private String channel = "WEB";
    private UUID sessionId = UUID.randomUUID();

    private com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession session;

    @BeforeEach
    void setUp() {
        session = com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession
            .initialize(tenantId, userId, channel);
        session.setId(sessionId);
    }

    @Test
    @DisplayName("Should create new session successfully")
    void shouldCreateNewSessionSuccessfully() {
        // Arrange
        CreateChatbotSessionCommand command = CreateChatbotSessionCommand.builder()
            .tenantId(tenantId)
            .userId(userId)
            .channel(channel)
            .metadata(Map.of("key", "value"))
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.save(eq(tenantId), any(com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession.class)))
            .thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.createSession(command);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionCreatedEvent.class));
    }

    @Test
    @DisplayName("Should create session without metadata")
    void shouldCreateSessionWithoutMetadata() {
        // Arrange
        CreateChatbotSessionCommand command = CreateChatbotSessionCommand.builder()
            .tenantId(tenantId)
            .userId(userId)
            .channel(channel)
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.createSession(command);

        // Assert
        assertNotNull(result);
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionCreatedEvent.class));
    }

    @Test
    @DisplayName("Should get session by ID")
    void shouldGetSessionById() {
        // Arrange
        GetChatbotSessionQuery query = new GetChatbotSessionQuery(tenantId, sessionId, false, false, false);

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.getSession(query);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        verify(sessionRepository).findById(tenantId, sessionId);
    }

    @Test
    @DisplayName("Should throw exception when session not found by ID")
    void shouldThrowExceptionWhenSessionNotFoundById() {
        // Arrange
        GetChatbotSessionQuery query = new GetChatbotSessionQuery(tenantId, sessionId, false, false, false);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChatbotSessionNotFoundException.class, () -> service.getSession(query));
        verify(sessionRepository).findById(tenantId, sessionId);
        verify(mapper, never()).toDto(any(com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession.class));
    }

    @Test
    @DisplayName("Should get session by session ID string")
    void shouldGetSessionBySessionIdString() {
        // Arrange
        String sessionIdString = session.getSessionId();
        GetChatbotSessionBySessionIdQuery query = new GetChatbotSessionBySessionIdQuery(tenantId, sessionIdString);

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findBySessionId(tenantId, sessionIdString)).thenReturn(Optional.of(session));
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.getSessionBySessionId(query);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        verify(sessionRepository).findBySessionId(tenantId, sessionIdString);
    }

    @Test
    @DisplayName("Should throw exception when session not found by session ID string")
    void shouldThrowExceptionWhenSessionNotFoundBySessionIdString() {
        // Arrange
        String sessionIdString = "non-existent-session";
        GetChatbotSessionBySessionIdQuery query = new GetChatbotSessionBySessionIdQuery(tenantId, sessionIdString);

        when(sessionRepository.findBySessionId(tenantId, sessionIdString)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChatbotSessionNotFoundException.class, () -> service.getSessionBySessionId(query));
        verify(sessionRepository).findBySessionId(tenantId, sessionIdString);
    }

    @Test
    @DisplayName("Should get user sessions without status filter")
    void shouldGetUserSessionsWithoutStatusFilter() {
        // Arrange
        GetUserSessionsQuery query = new GetUserSessionsQuery(tenantId, userId, null, 0, 20, "createdAt", "desc");

        List<com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession> sessions = List.of(session);

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findByUserId(tenantId, userId)).thenReturn(sessions);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        List<ChatbotSessionDto> result = service.getUserSessions(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionId, result.get(0).getId());
        verify(sessionRepository).findByUserId(tenantId, userId);
    }

    @Test
    @DisplayName("Should get user sessions with status filter")
    void shouldGetUserSessionsWithStatusFilter() {
        // Arrange
        GetUserSessionsQuery query = new GetUserSessionsQuery(tenantId, userId, "ACTIVE", 0, 20, "createdAt", "desc");

        com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession otherSession =
            com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession
                .initialize(tenantId, "other-user", channel);

        List<com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession> allSessions = List.of(session, otherSession);

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findByStatus(tenantId, "ACTIVE")).thenReturn(allSessions);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        List<ChatbotSessionDto> result = service.getUserSessions(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionId, result.get(0).getId());
        verify(sessionRepository).findByStatus(tenantId, "ACTIVE");
    }

    @Test
    @DisplayName("Should send message to initialized session")
    void shouldSendMessageToInitializedSession() {
        // Arrange
        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .direction(MessageDirection.INBOUND)
            .content("Hello")
            .messageType("USER")
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);
        dto.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.sendMessage(command);

        // Assert
        assertNotNull(result);
        assertEquals(SessionStatus.ACTIVE, result.getStatus());
        assertEquals(1, session.getMessageCount());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionActivatedEvent.class));
        verify(eventPublisher).publish(any(ChatbotMessageReceivedEvent.class));
    }

    @Test
    @DisplayName("Should send message to active session")
    void shouldSendMessageToActiveSession() {
        // Arrange
        session.activate();

        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .direction(MessageDirection.INBOUND)
            .content("Hello again")
            .messageType("USER")
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.sendMessage(command);

        // Assert
        assertNotNull(result);
        assertEquals(1, session.getMessageCount());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher, never()).publish(any(ChatbotSessionActivatedEvent.class));
        verify(eventPublisher).publish(any(ChatbotMessageReceivedEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when sending message to completed session")
    void shouldThrowExceptionWhenSendingMessageToCompletedSession() {
        // Arrange
        session.activate();
        session.complete();

        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .direction(MessageDirection.INBOUND)
            .content("Hello")
            .messageType("USER")
            .build();

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(InvalidChatbotSessionException.class, () -> service.sendMessage(command));
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when sending message with invalid content")
    void shouldThrowExceptionWhenSendingMessageWithInvalidContent() {
        // Arrange
        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .direction(MessageDirection.INBOUND)
            .content("") // Empty content
            .messageType("USER")
            .build();

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(command));
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when session not found for message")
    void shouldThrowExceptionWhenSessionNotFoundForMessage() {
        // Arrange
        UUID nonExistentSessionId = UUID.randomUUID();

        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(nonExistentSessionId)
            .direction(MessageDirection.INBOUND)
            .content("Hello")
            .messageType("USER")
            .build();

        when(sessionRepository.findById(tenantId, nonExistentSessionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChatbotSessionNotFoundException.class, () -> service.sendMessage(command));
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should update session status to ACTIVE")
    void shouldUpdateSessionStatusToActive() {
        // Arrange
        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .status(SessionStatus.ACTIVE)
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);
        dto.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.updateStatus(command);

        // Assert
        assertNotNull(result);
        assertEquals(SessionStatus.ACTIVE, result.getStatus());
        verify(sessionRepository).save(eq(tenantId), any());
    }

    @Test
    @DisplayName("Should update session status to COMPLETED")
    void shouldUpdateSessionStatusToCompleted() {
        // Arrange
        session.activate();

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .status(SessionStatus.COMPLETED)
            .reason("Test completion")
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);
        dto.setStatus(SessionStatus.COMPLETED);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.updateStatus(command);

        // Assert
        assertNotNull(result);
        assertEquals(SessionStatus.COMPLETED, result.getStatus());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionCompletedEvent.class));
    }

    @Test
    @DisplayName("Should update session status to TERMINATED")
    void shouldUpdateSessionStatusToTerminated() {
        // Arrange
        session.activate();

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .status(SessionStatus.TERMINATED)
            .reason("Test termination")
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);
        dto.setStatus(SessionStatus.TERMINATED);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.updateStatus(command);

        // Assert
        assertNotNull(result);
        assertEquals(SessionStatus.TERMINATED, result.getStatus());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionTerminatedEvent.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Arrange
        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .status(SessionStatus.COMPLETED) // Can't complete from INITIALIZED
            .build();

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(InvalidChatbotSessionException.class, () -> service.updateStatus(command));
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should add context to session")
    void shouldAddContextToSession() {
        // Arrange
        AddContextCommand command = AddContextCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .contextKey("test-key")
            .contextValue("test-value")
            .contextType("USER_DEFINED")
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(contextRepository.save(eq(tenantId), any())).thenReturn(any());
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.addContext(command);

        // Assert
        assertNotNull(result);
        assertEquals(1, session.getContexts().size());
        verify(sessionRepository).save(eq(tenantId), any());
        verify(contextRepository).save(eq(tenantId), any());
    }

    @Test
    @DisplayName("Should throw exception when adding context to non-existent session")
    void shouldThrowExceptionWhenAddingContextToNonExistentSession() {
        // Arrange
        UUID nonExistentSessionId = UUID.randomUUID();

        AddContextCommand command = AddContextCommand.builder()
            .tenantId(tenantId)
            .sessionId(nonExistentSessionId)
            .contextKey("test-key")
            .contextValue("test-value")
            .contextType("USER_DEFINED")
            .build();

        when(sessionRepository.findById(tenantId, nonExistentSessionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChatbotSessionNotFoundException.class, () -> service.addContext(command));
        verify(contextRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should delete session successfully")
    void shouldDeleteSessionSuccessfully() {
        // Arrange
        when(sessionRepository.exists(tenantId, sessionId)).thenReturn(true);
        doNothing().when(messageRepository).deleteBySessionId(tenantId, sessionId);
        doNothing().when(contextRepository).deleteBySessionId(tenantId, sessionId);
        doNothing().when(sessionRepository).delete(tenantId, sessionId);

        // Act
        service.deleteSession(tenantId, sessionId);

        // Assert
        verify(messageRepository).deleteBySessionId(tenantId, sessionId);
        verify(contextRepository).deleteBySessionId(tenantId, sessionId);
        verify(sessionRepository).delete(tenantId, sessionId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent session")
    void shouldThrowExceptionWhenDeletingNonExistentSession() {
        // Arrange
        UUID nonExistentSessionId = UUID.randomUUID();
        when(sessionRepository.exists(tenantId, nonExistentSessionId)).thenReturn(false);

        // Act & Assert
        assertThrows(ChatbotSessionNotFoundException.class, () -> service.deleteSession(tenantId, nonExistentSessionId));
        verify(messageRepository, never()).deleteBySessionId(any(), any());
        verify(contextRepository, never()).deleteBySessionId(any(), any());
        verify(sessionRepository, never()).delete(any(), any());
    }

    @Test
    @DisplayName("Should get active sessions")
    void shouldGetActiveSessions() {
        // Arrange
        com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession activeSession =
            com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession
                .initialize(tenantId, userId, channel);
        activeSession.activate();

        List<com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession> activeSessions = List.of(activeSession);

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(activeSession.getId());

        when(sessionRepository.findActiveSessions(tenantId)).thenReturn(activeSessions);
        when(mapper.toDto(activeSession)).thenReturn(dto);

        // Act
        List<ChatbotSessionDto> result = service.getActiveSessions(tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sessionRepository).findActiveSessions(tenantId);
    }

    @Test
    @DisplayName("Should process idle sessions")
    void shouldProcessIdleSessions() {
        // Arrange
        com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession idleSession =
            com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession
                .initialize(tenantId, userId, channel);
        idleSession.activate();
        // Simulate idle by setting old activity time
        idleSession.setLastActivityAt(LocalDateTime.now().minusMinutes(10));

        List<com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession> idleSessions = List.of(idleSession);

        when(sessionRepository.findIdleSessions(tenantId, 5)).thenReturn(idleSessions);
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(idleSession);

        // Act
        int processedCount = service.processIdleSessions(tenantId, 5);

        // Assert
        assertEquals(1, processedCount);
        verify(sessionRepository).findIdleSessions(tenantId, 5);
        verify(sessionRepository).save(eq(tenantId), any());
        verify(eventPublisher).publish(any(ChatbotSessionCompletedEvent.class));
    }

    @Test
    @DisplayName("Should return zero when no idle sessions found")
    void shouldReturnZeroWhenNoIdleSessionsFound() {
        // Arrange
        when(sessionRepository.findIdleSessions(tenantId, 5)).thenReturn(List.of());

        // Act
        int processedCount = service.processIdleSessions(tenantId, 5);

        // Assert
        assertEquals(0, processedCount);
        verify(sessionRepository).findIdleSessions(tenantId, 5);
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should handle message with metadata")
    void shouldHandleMessageWithMetadata() {
        // Arrange
        session.activate();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("lang", "en");

        SendMessageCommand command = SendMessageCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .direction(MessageDirection.INBOUND)
            .content("Hello")
            .messageType("USER")
            .metadata(metadata)
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.sendMessage(command);

        // Assert
        assertNotNull(result);
        ChatMessage addedMessage = session.getMessages().get(0);
        assertEquals(metadata, addedMessage.getMetadata());
    }

    @Test
    @DisplayName("Should handle context with TTL")
    void shouldAddContextWithTTL() {
        // Arrange
        Integer ttl = 3600;

        AddContextCommand command = AddContextCommand.builder()
            .tenantId(tenantId)
            .sessionId(sessionId)
            .contextKey("temp-key")
            .contextValue("temp-value")
            .contextType("USER_DEFINED")
            .ttl(ttl)
            .build();

        ChatbotSessionDto dto = new ChatbotSessionDto();
        dto.setId(sessionId);

        when(sessionRepository.findById(tenantId, sessionId)).thenReturn(Optional.of(session));
        when(contextRepository.save(eq(tenantId), any())).thenReturn(any());
        when(sessionRepository.save(eq(tenantId), any())).thenReturn(session);
        when(mapper.toDto(session)).thenReturn(dto);

        // Act
        ChatbotSessionDto result = service.addContext(command);

        // Assert
        assertNotNull(result);
        assertEquals(ttl, session.getContexts().get(0).getTtl());
        verify(contextRepository).save(eq(tenantId), any());
    }
}
