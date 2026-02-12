package com.gogidix.rapidassist.orchestration.fleetorganization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Fleet Organization Service Application
 * Port: 8086
 * Database: orchestration_fleet_organization_service_db
 *
 * Manages fleet organizational hierarchy, units, and policies
 */
@SpringBootApplication
@EnableMongoAuditing
public class FleetOrganizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetOrganizationServiceApplication.class, args);
    }
}
