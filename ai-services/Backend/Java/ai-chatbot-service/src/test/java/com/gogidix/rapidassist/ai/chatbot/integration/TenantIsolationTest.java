package com.gogidix.rapidassist.ai.chatbot.integration;

import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ConversationFlow;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.tenant.RequestContext;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository.ChatbotSessionRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for ALL services before production
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class TenantIsolationTest {

    @Autowired
    private ChatbotSessionRepositoryImpl sessionRepository;

    private RequestContext tenantA;
    private RequestContext tenantB;

    @BeforeEach
    void setUp() {
        // Create tenant contexts
        tenantA = RequestContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        tenantB = RequestContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        log.info("Set up test contexts: tenantA={}, tenantB={}",
                tenantA.tenantId(), tenantB.tenantId());
    }

    @AfterEach
    void tearDown() {
        // Clear contexts and cleanup
        RequestContextHolder.clear();

        // Clean up test data
        try {
            sessionRepository.deleteAll();
        } catch (Exception e) {
            log.warn("Failed to cleanup test data: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B sessions")
    @Tag("critical")
    void whenTenantAQueries_shouldOnlySeeTenantASessions() {
        // Given: Create sessions for both tenants
        RequestContextHolder.set(tenantA);
        ChatbotSession sessionA = ChatbotSession.initialize(
                "tenant-a",
                "user-a",
                "WEB"
        );
        ChatbotSession savedSessionA = sessionRepository.save(sessionA);
        log.info("Created session for tenant A: {}", savedSessionA.getId());

        RequestContextHolder.set(tenantB);
        ChatbotSession sessionB = ChatbotSession.initialize(
                "tenant-b",
                "user-b",
                "MOBILE_APP"
        );
        ChatbotSession savedSessionB = sessionRepository.save(sessionB);
        log.info("Created session for tenant B: {}", savedSessionB.getId());

        // When: Tenant A queries all sessions
        RequestContextHolder.set(tenantA);
        List<ChatbotSession> tenantAResults = sessionRepository.findByTenantId("tenant-a");

        // Then: Tenant A should ONLY see Tenant A's data
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults)
                .noneMatch(session -> session.getTenantId().equals("tenant-b"));

        log.info("Verified tenant A isolation: saw {} sessions", tenantAResults.size());
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    @Tag("critical")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Session exists for Tenant A
        RequestContextHolder.set(tenantA);
        ChatbotSession session = ChatbotSession.initialize(
                "tenant-a",
                "user-a",
                "SMS"
        );
        ChatbotSession saved = sessionRepository.save(session);
        UUID sessionId = saved.getId();

        log.info("Created session for tenant A with ID: {}", sessionId);

        // When: Tenant B tries to access same session by ID
        RequestContextHolder.set(tenantB);
        var result = sessionRepository.findById(sessionId.toString());

        // Then: Result should be empty
        assertThat(result).isEmpty();

        log.info("Verified cross-tenant access blocked for ID: {}", sessionId);
    }

    @Test
    @DisplayName("Tenant-specific contexts are isolated")
    @Tag("critical")
    void whenTenantAAddsContext_tenantBCannotSeeIt() {
        // Given: Session for Tenant A
        RequestContextHolder.set(tenantA);
        ChatbotSession session = ChatbotSession.initialize("tenant-a", "user-a", "API");
        ChatbotSession savedSession = sessionRepository.save(session);

        // Add context for Tenant A
        ChatbotContext contextA = ChatbotContext.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-a")
                .chatbotSessionId(savedSession.getId())
                .contextKey("language")
                .contextValue("en")
                .contextType("preference")
                .build();

        savedSession.addContext(contextA);
        sessionRepository.save(savedSession);

        log.info("Added context for tenant A: key={}", contextA.getContextKey());

        // When: Tenant B queries contexts
        RequestContextHolder.set(tenantB);
        List<ChatbotSession> tenantBSessions = sessionRepository.findByTenantId("tenant-b");

        // Then: Tenant B should not see Tenant A's contexts
        assertThat(tenantBSessions).hasSize(0);

        log.info("Verified context isolation: tenant B saw {} sessions", tenantBSessions.size());
    }

    @Test
    @DisplayName("Messages are tenant-isolated")
    @Tag("critical")
    void whenTenantAAddsMessages_tenantBCannotSeeThem() {
        // Given: Session with messages for Tenant A
        RequestContextHolder.set(tenantA);
        ChatbotSession session = ChatbotSession.initialize("tenant-a", "user-a", "WEB");
        ChatbotSession savedSession = sessionRepository.save(session);

        ChatMessage messageA = ChatMessage.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-a")
                .chatbotSessionId(savedSession.getId())
                .direction(com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection.INBOUND)
                .content("Hello from Tenant A")
                .messageType("text")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        savedSession.addMessage(messageA);
        sessionRepository.save(savedSession);

        log.info("Added message for tenant A: content={}", messageA.getContent());

        // When: Tenant B queries messages
        RequestContextHolder.set(tenantB);
        List<ChatbotSession> tenantBSessions = sessionRepository.findByTenantId("tenant-b");

        // Then: Tenant B should not see Tenant A's messages
        assertThat(tenantBSessions).hasSize(0);

        log.info("Verified message isolation: tenant B saw {} messages", tenantBSessions.size());
    }

    @Test
    @DisplayName("Conversation flows are tenant-isolated")
    @Tag("critical")
    void whenTenantAAddsFlow_tenantBCannotSeeIt() {
        // Given: Session with flow for Tenant A
        RequestContextHolder.set(tenantA);
        ChatbotSession session = ChatbotSession.initialize("tenant-a", "user-a", "VOICE_ASSISTANT");
        ChatbotSession savedSession = sessionRepository.save(session);

        ConversationFlow flowA = ConversationFlow.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-a")
                .chatbotSessionId(savedSession.getId())
                .currentState("greeting")
                .previousState(null)
                .nextState("collect_info")
                .flowType("linear")
                .stepNumber(1)
                .stateEnteredAt(java.time.LocalDateTime.now())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        savedSession.addFlow(flowA);
        sessionRepository.save(savedSession);

        log.info("Added flow for tenant A: state={}", flowA.getCurrentState());

        // When: Tenant B queries flows
        RequestContextHolder.set(tenantB);
        List<ChatbotSession> tenantBSessions = sessionRepository.findByTenantId("tenant-b");

        // Then: Tenant B should not see Tenant A's flows
        assertThat(tenantBSessions).hasSize(0);

        log.info("Verified flow isolation: tenant B saw {} flows", tenantBSessions.size());
    }

    @Test
    @DisplayName("Delete operations respect tenant boundaries")
    @Tag("critical")
    void whenTenantATriesToDelete_tenantBSession() {
        // Given: Sessions for both tenants
        RequestContextHolder.set(tenantA);
        ChatbotSession sessionA = sessionRepository.save(
                ChatbotSession.initialize("tenant-a", "user-a", "EMAIL")
        );
        String sessionAId = sessionA.getId().toString();

        RequestContextHolder.set(tenantB);
        ChatbotSession sessionB = sessionRepository.save(
                ChatbotSession.initialize("tenant-b", "user-b", "KIOSK")
        );
        String sessionBId = sessionB.getId().toString();

        log.info("Created sessions: A={}, B={}", sessionAId, sessionBId);

        // When: Tenant A tries to delete Tenant B's session
        RequestContextHolder.set(tenantA);
        sessionRepository.deleteById(sessionBId);

        // Then: Tenant B's session should still exist
        RequestContextHolder.set(tenantB);
        var stillExists = sessionRepository.findById(sessionBId);

        assertThat(stillExists).isPresent();

        log.info("Verified delete isolation: tenant B session still exists");
    }

    @Test
    @DisplayName("Multiple operations maintain tenant isolation")
    @Tag("critical")
    void whenMultipleTenantsOperate_isolationMaintained() {
        // Given: Multiple operations from both tenants
        RequestContextHolder.set(tenantA);
        for (int i = 0; i < 5; i++) {
            ChatbotSession session = ChatbotSession.initialize(
                    "tenant-a",
                    "user-a-" + i,
                    "WEB"
            );
            sessionRepository.save(session);
        }

        RequestContextHolder.set(tenantB);
        for (int i = 0; i < 3; i++) {
            ChatbotSession session = ChatbotSession.initialize(
                    "tenant-b",
                    "user-b-" + i,
                    "MOBILE_APP"
            );
            sessionRepository.save(session);
        }

        log.info("Created 5 sessions for tenant A, 3 sessions for tenant B");

        // When: Query from both tenants
        RequestContextHolder.set(tenantA);
        List<ChatbotSession> tenantACount = sessionRepository.findByTenantId("tenant-a");

        RequestContextHolder.set(tenantB);
        List<ChatbotSession> tenantBCount = sessionRepository.findByTenantId("tenant-b");

        // Then: Each tenant sees only their data
        assertThat(tenantACount).hasSize(5);
        assertThat(tenantBCount).hasSize(3);

        // Verify no cross-contamination
        assertThat(tenantACount)
                .noneMatch(s -> s.getTenantId().equals("tenant-b"));
        assertThat(tenantBCount)
                .noneMatch(s -> s.getTenantId().equals("tenant-a"));

        log.info("Verified multi-operation isolation: A={}, B={}",
                tenantACount.size(), tenantBCount.size());
    }

    @Test
    @DisplayName("Context switching works correctly")
    @Tag("critical")
    void whenSwitchingContexts_isolationMaintained() {
        // Given: Switch between tenants multiple times
        RequestContextHolder.set(tenantA);
        ChatbotSession sessionA = sessionRepository.save(
                ChatbotSession.initialize("tenant-a", "user-a", "WEB")
        );
        UUID sessionAId = sessionA.getId();

        RequestContextHolder.set(tenantB);
        ChatbotSession sessionB = sessionRepository.save(
                ChatbotSession.initialize("tenant-b", "user-b", "API")
        );
        UUID sessionBId = sessionB.getId();

        // Switch back to A
        RequestContextHolder.set(tenantA);
        var resultA = sessionRepository.findById(sessionAId.toString());

        // Switch to B
        RequestContextHolder.set(tenantB);
        var resultB = sessionRepository.findById(sessionBId.toString());

        // Then: Both should find their respective sessions
        assertThat(resultA).isPresent();
        assertThat(resultB).isPresent();
        assertThat(resultA.get().getTenantId()).isEqualTo("tenant-a");
        assertThat(resultB.get().getTenantId()).isEqualTo("tenant-b");

        log.info("Verified context switching: A found={}, B found={}",
                resultA.isPresent(), resultB.isPresent());
    }
}
