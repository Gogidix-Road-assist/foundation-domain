package com.gogidix.rapidassist.notification.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.notification.service.application.service.ComprehensiveNotificationService;
import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComprehensiveNotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.create(
            "tenant-123", Notification.NotificationType.ALERT,
            List.of("user@example.com"), "Test Subject", "Test Content"
        );
    }

    @Test
    void health_ReturnsUpStatus() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("notification-service"));
    }

    @Test
    void sendImmediate_Success() throws Exception {
        when(notificationService.sendImmediate(
                eq("tenant-123"), eq(Notification.NotificationType.ALERT),
                any(), eq("Test Subject"), eq("Test Content"),
                any()
        )).thenReturn(testNotification);

        NotificationController.SendImmediateRequest request = new NotificationController.SendImmediateRequest(
                "tenant-123", Notification.NotificationType.ALERT,
                List.of("user@example.com"), "Test Subject", "Test Content",
                List.of(Notification.NotificationChannel.EMAIL)
        );

        mockMvc.perform(post("/api/notifications/send/immediate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value("notif-123"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void getPendingNotifications_Success() throws Exception {
        when(notificationService.getPendingNotifications("tenant-123"))
                .thenReturn(List.of(testNotification));

        mockMvc.perform(get("/api/notifications/pending")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value("notif-123"));
    }

    @Test
    void getScheduledNotifications_EmptyList() throws Exception {
        when(notificationService.getScheduledNotifications("tenant-123"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/scheduled")
                        .param("tenantId", "tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
