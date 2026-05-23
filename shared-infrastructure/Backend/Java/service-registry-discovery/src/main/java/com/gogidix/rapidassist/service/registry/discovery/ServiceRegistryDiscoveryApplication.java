package com.gogidix.rapidassist.service.registry.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryDiscoveryApplication.class, args);
    }
}
