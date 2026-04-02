package com.gogidix.rapidassist.event.audit.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EventAuditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventAuditServiceApplication.class, args);
    }
}
