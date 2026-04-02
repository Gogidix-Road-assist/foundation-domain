package com.gogidix.ai.dashboard.configuration;

import com.gogidix.ai.dashboard.handler.DashboardWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket Configuration for real-time dashboard updates
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DashboardWebSocketHandler dashboardWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dashboardWebSocketHandler, "/ws")
            .setAllowedOrigins("*")
            .withSockJS();
    }
}
