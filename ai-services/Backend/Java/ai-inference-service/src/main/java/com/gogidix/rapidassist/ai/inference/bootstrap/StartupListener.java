package com.gogidix.rapidassist.ai.inference.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Application startup listener
 * Logs when the AI Inference Service is ready
 */
@Slf4j
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("============================================");
        log.info("AI Inference Service is READY!");
        log.info("Inference endpoints are now available");
        log.info("============================================");
    }
}
