package com.gogidix.rapidassist.orchestration.fleet_policy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Fleet Policy Service
 * Manages fleet policies, compliance tracking, and violation monitoring
 *
 * @author Gogidix
 * @version 1.0.0
 */
@SpringBootApplication
@EnableKafka
@EnableScheduling
public class FleetPolicyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetPolicyServiceApplication.class, args);
    }
}
