package com.gogidix.ai.dashboard.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.ai.dashboard.model.ServiceHealth;
import com.gogidix.ai.dashboard.service.HealthCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket Handler for real-time dashboard updates
 */
@Slf4j
@Component
public class DashboardWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HealthCheckService healthCheckService;

    public DashboardWebSocketHandler(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("WebSocket connection established. Total sessions: {}", sessions.size());

        // Send initial data
        sendHealthUpdate(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket connection closed. Total sessions: {}", sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received WebSocket message: {}", payload);

        // Handle client requests
        try {
            Map<String, String> request = objectMapper.readValue(payload, Map.class);
            String action = request.get("action");

            if ("refresh".equals(action)) {
                sendHealthUpdate(session);
            } else if ("ping".equals(action)) {
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket message", e);
        }
    }

    /**
     * Broadcast health updates to all connected clients every 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    public void broadcastHealthUpdates() {
        if (sessions.isEmpty()) {
            return;
        }

        log.debug("Broadcasting health updates to {} clients", sessions.size());

        try {
            List<ServiceHealth> healthList = healthCheckService.getAllLatestHealth();
            String message = objectMapper.writeValueAsString(Map.of(
                "type", "health_update",
                "timestamp", LocalDateTime.now().toString(),
                "data", healthList
            ));

            TextMessage textMessage = new TextMessage(message);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("Error sending message to session", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error broadcasting health updates", e);
        }
    }

    /**
     * Send initial health update to a newly connected session
     */
    private void sendHealthUpdate(WebSocketSession session) throws IOException {
        try {
            List<ServiceHealth> healthList = healthCheckService.getAllLatestHealth();

            if (healthList.isEmpty()) {
                // Trigger health check if cache is empty
                healthCheckService.checkAllServices().thenAccept(health -> {
                    try {
                        String message = objectMapper.writeValueAsString(Map.of(
                            "type", "health_update",
                            "timestamp", LocalDateTime.now().toString(),
                            "data", health
                        ));
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("Error sending initial health update", e);
                    }
                });
            } else {
                String message = objectMapper.writeValueAsString(Map.of(
                    "type", "health_update",
                    "timestamp", LocalDateTime.now().toString(),
                    "data", healthList
                ));
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            log.error("Error sending health update", e);
        }
    }

    /**
     * Broadcast alert to all connected clients
     */
    public void broadcastAlert(String serviceId, String alertType, String message) {
        try {
            String alertMessage = objectMapper.writeValueAsString(Map.of(
                "type", "alert",
                "timestamp", LocalDateTime.now().toString(),
                "data", Map.of(
                    "serviceId", serviceId,
                    "alertType", alertType,
                    "message", message
                )
            ));

            TextMessage textMessage = new TextMessage(alertMessage);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("Error sending alert to session", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error broadcasting alert", e);
        }
    }

    /**
     * Get connected sessions count
     */
    public int getConnectedSessionsCount() {
        return sessions.size();
    }
}
