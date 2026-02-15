package com.gogidix.rapidassist.ai.translation.application.service;

import com.gogidix.rapidassist.ai.translation.application.command.*;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationSessionDto;
import com.gogidix.rapidassist.ai.translation.application.mapper.TranslationSessionMapper;
import com.gogidix.rapidassist.ai.translation.application.query.GetTranslationSessionQuery;
import com.gogidix.rapidassist.ai.translation.application.query.GetUserSessionsQuery;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession.SessionStatus;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession.SessionType;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.domain.repository.TranslationSessionRepositoryPort;
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
 * Unit tests for TranslationSessionApplicationService
 * Tests translation session management and translation operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TranslationSessionApplicationService Tests")
class TranslationSessionApplicationServiceTest {

    @Mock
    private TranslationSessionRepositoryPort sessionRepository;

    @Mock
    private TranslationSessionMapper mapper;

    @InjectMocks
    private TranslationSessionApplicationService applicationService;

    private TranslationSession testSession;
    private TranslationSessionDto testSessionDto;
    private UUID sessionId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        testSession = TranslationSession.builder()
                .id(sessionId)
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.REAL_TIME)
                .defaultSourceLanguage("en")
                .defaultTargetLanguage("es")
                .status(SessionStatus.ACTIVE)
                .channel("web")
                .translationRequests(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testSessionDto = TranslationSessionDto.builder()
                .id(sessionId)
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.REAL_TIME)
                .defaultSourceLanguage("en")
                .defaultTargetLanguage("es")
                .status(SessionStatus.ACTIVE)
                .channel("web")
                .translationRequests(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create translation session successfully")
    void testCreateSession() {
        CreateTranslationSessionCommand command = CreateTranslationSessionCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.REAL_TIME)
                .sourceLanguage("en")
                .targetLanguage("es")
                .channel("web")
                .metadata(Map.of("source", "api"))
                .build();

        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.createSession(command);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(userId, result.getUserId());

        verify(sessionRepository, times(1)).save(eq(tenantId), any(TranslationSession.class));
        verify(mapper, times(1)).toDto(testSession);
    }

    @Test
    @DisplayName("Should get translation session by ID")
    void testGetSession() {
        GetTranslationSessionQuery query = GetTranslationSessionQuery.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.getSession(query);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());

        verify(sessionRepository, times(1)).findById(tenantId, sessionId);
        verify(mapper, times(1)).toDto(testSession);
    }

    @Test
    @DisplayName("Should throw exception when session not found")
    void testGetSessionNotFound() {
        GetTranslationSessionQuery query = GetTranslationSessionQuery.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.getSession(query);
        });

        verify(sessionRepository, times(1)).findById(tenantId, sessionId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get user sessions without status filter")
    void testGetUserSessionsWithoutFilter() {
        GetUserSessionsQuery query = GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .build();

        List<TranslationSession> sessions = List.of(testSession);

        when(sessionRepository.findByUserId(tenantId, userId)).thenReturn(sessions);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        List<TranslationSessionDto> results = applicationService.getUserSessions(query);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(sessionRepository, times(1)).findByUserId(tenantId, userId);
        verify(mapper, times(1)).toDto(testSession);
    }

    @Test
    @DisplayName("Should get user sessions with status filter")
    void testGetUserSessionsWithFilter() {
        GetUserSessionsQuery query = GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status("ACTIVE")
                .build();

        List<TranslationSession> sessions = List.of(testSession);

        when(sessionRepository.findByUserId(tenantId, userId)).thenReturn(sessions);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        List<TranslationSessionDto> results = applicationService.getUserSessions(query);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(sessionRepository, times(1)).findByUserId(tenantId, userId);
    }

    @Test
    @DisplayName("Should return empty list when no sessions found")
    void testGetUserSessionsEmpty() {
        GetUserSessionsQuery query = GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .build();

        when(sessionRepository.findByUserId(tenantId, userId)).thenReturn(List.of());

        List<TranslationSessionDto> results = applicationService.getUserSessions(query);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(sessionRepository, times(1)).findByUserId(tenantId, userId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should translate text successfully")
    void testTranslateText() {
        TranslateTextCommand command = TranslateTextCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .sourceText("Hello, world!")
                .sourceLanguage("en")
                .targetLanguage("es")
                .metadata(Map.of("key", "value"))
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.translateText(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).findById(tenantId, sessionId);
        verify(sessionRepository, times(1)).save(eq(tenantId), argThat(s ->
                !s.getTranslationRequests().isEmpty()
        ));
    }

    @Test
    @DisplayName("Should throw exception when translating text for non-existent session")
    void testTranslateTextSessionNotFound() {
        TranslateTextCommand command = TranslateTextCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .sourceText("Hello")
                .sourceLanguage("en")
                .targetLanguage("es")
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.translateText(command);
        });

        verify(sessionRepository, times(1)).findById(tenantId, sessionId);
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should update session status to active")
    void testUpdateStatusToActive() {
        testSession.setStatus(SessionStatus.PAUSED);

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(SessionStatus.ACTIVE)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.updateStatus(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), argThat(s ->
                SessionStatus.ACTIVE == s.getStatus()
        ));
    }

    @Test
    @DisplayName("Should update session status to paused")
    void testUpdateStatusToPaused() {
        testSession.setStatus(SessionStatus.ACTIVE);

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(SessionStatus.PAUSED)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.updateStatus(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), argThat(s ->
                SessionStatus.PAUSED == s.getStatus()
        ));
    }

    @Test
    @DisplayName("Should update session status to completed")
    void testUpdateStatusToCompleted() {
        testSession.setStatus(SessionStatus.ACTIVE);

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(SessionStatus.COMPLETED)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.updateStatus(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), argThat(s ->
                SessionStatus.COMPLETED == s.getStatus()
        ));
    }

    @Test
    @DisplayName("Should update session status to terminated")
    void testUpdateStatusToTerminated() {
        testSession.setStatus(SessionStatus.ACTIVE);

        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(SessionStatus.TERMINATED)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.updateStatus(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), argThat(s ->
                SessionStatus.TERMINATED == s.getStatus()
        ));
    }

    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void testUpdateStatusInvalidTransition() {
        // Set session to COMPLETED status
        testSession.setStatus(SessionStatus.COMPLETED);

        // Try to transition back to PENDING (invalid transition)
        UpdateSessionStatusCommand command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(SessionStatus.PENDING)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));

        assertThrows(IllegalStateException.class, () -> {
            applicationService.updateStatus(command);
        });

        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should delete session successfully")
    void testDeleteSession() {
        doNothing().when(sessionRepository).delete(tenantId, sessionId);

        applicationService.deleteSession(tenantId, sessionId);

        verify(sessionRepository, times(1)).delete(tenantId, sessionId);
    }

    @Test
    @DisplayName("Should get active sessions")
    void testGetActiveSessions() {
        List<TranslationSession> activeSessions = List.of(
                testSession,
                createActiveSession("id-2", tenantId, "user-2")
        );

        when(sessionRepository.findActiveSessions(tenantId)).thenReturn(activeSessions);
        when(mapper.toDto(any())).thenReturn(testSessionDto);

        List<TranslationSessionDto> results = applicationService.getActiveSessions(tenantId);

        assertNotNull(results);
        assertEquals(2, results.size());

        verify(sessionRepository, times(1)).findActiveSessions(tenantId);
        verify(mapper, times(2)).toDto(any());
    }

    @Test
    @DisplayName("Should return empty list when no active sessions")
    void testGetActiveSessionsEmpty() {
        when(sessionRepository.findActiveSessions(tenantId)).thenReturn(List.of());

        List<TranslationSessionDto> results = applicationService.getActiveSessions(tenantId);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(sessionRepository, times(1)).findActiveSessions(tenantId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should process idle sessions")
    void testProcessIdleSessions() {
        List<TranslationSession> idleSessions = List.of(
                testSession,
                createActiveSession("id-2", tenantId, "user-2")
        );

        when(sessionRepository.findIdleSessions(tenantId, 30))
                .thenReturn(idleSessions);
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);

        int processedCount = applicationService.processIdleSessions(tenantId, 30);

        assertEquals(2, processedCount);
        verify(sessionRepository, times(1)).findIdleSessions(tenantId, 30);
        verify(sessionRepository, times(2)).save(eq(tenantId), any(TranslationSession.class));
    }

    @Test
    @DisplayName("Should return 0 when no idle sessions to process")
    void testProcessIdleSessionsNone() {
        when(sessionRepository.findIdleSessions(tenantId, 30))
                .thenReturn(List.of());

        int processedCount = applicationService.processIdleSessions(tenantId, 30);

        assertEquals(0, processedCount);
        verify(sessionRepository, times(1)).findIdleSessions(tenantId, 30);
        verify(sessionRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should handle translate text without metadata")
    void testTranslateTextWithoutMetadata() {
        TranslateTextCommand command = TranslateTextCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .sourceText("Hello")
                .sourceLanguage("en")
                .targetLanguage("es")
                .metadata(null)
                .build();

        when(sessionRepository.findById(tenantId, sessionId))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.translateText(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), any(TranslationSession.class));
    }

    @Test
    @DisplayName("Should handle create session without metadata")
    void testCreateSessionWithoutMetadata() {
        CreateTranslationSessionCommand command = CreateTranslationSessionCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.BATCH)
                .sourceLanguage("en")
                .targetLanguage("fr")
                .channel("api")
                .metadata(null)
                .build();

        when(sessionRepository.save(eq(tenantId), any(TranslationSession.class)))
                .thenReturn(testSession);
        when(mapper.toDto(testSession)).thenReturn(testSessionDto);

        TranslationSessionDto result = applicationService.createSession(command);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(eq(tenantId), any(TranslationSession.class));
    }

    @Test
    @DisplayName("Should filter user sessions by status correctly")
    void testFilterUserSessionsByStatus() {
        TranslationSession activeSession = createActiveSession(sessionId.toString(), tenantId, userId);
        TranslationSession completedSession = createCompletedSession("id-2", tenantId, userId);

        List<TranslationSession> allSessions = Arrays.asList(activeSession, completedSession);

        GetUserSessionsQuery query = GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status("ACTIVE")
                .build();

        when(sessionRepository.findByUserId(tenantId, userId)).thenReturn(allSessions);
        when(mapper.toDto(any())).thenReturn(testSessionDto);

        List<TranslationSessionDto> results = applicationService.getUserSessions(query);

        assertNotNull(results);
        assertEquals(1, results.size()); // Only active session
    }

    private TranslationSession createActiveSession(String id, String tenantId, String userId) {
        return TranslationSession.builder()
                .id(UUID.fromString(id.replace("id-", "00000000-0000-0000-0000-00000000")))
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.REAL_TIME)
                .defaultSourceLanguage("en")
                .defaultTargetLanguage("es")
                .status(SessionStatus.ACTIVE)
                .channel("web")
                .translationRequests(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();
    }

    private TranslationSession createCompletedSession(String id, String tenantId, String userId) {
        return TranslationSession.builder()
                .id(UUID.fromString(id.replace("id-", "00000000-0000-0000-0000-00000000")))
                .tenantId(tenantId)
                .userId(userId)
                .sessionType(SessionType.BATCH)
                .defaultSourceLanguage("en")
                .defaultTargetLanguage("fr")
                .status(SessionStatus.COMPLETED)
                .channel("api")
                .translationRequests(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();
    }
}
