package com.gogidix.rapidassist.ai.chatbot.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Application startup listener for chatbot AI Service
 */
@Slf4j
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("{} AI Service started successfully", "chatbot");
        log.info("Service is ready to accept requests");
        initializeService();
    }

    /**
     * Perform service-specific initialization
     */
    private void initializeService() {
        log.info("Initializing chatbot service components");
    }
}
