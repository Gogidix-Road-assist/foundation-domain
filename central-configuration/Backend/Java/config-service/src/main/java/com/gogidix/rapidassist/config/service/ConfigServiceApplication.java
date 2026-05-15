package com.gogidix.rapidassist.config.service;

import com.gogidix.rapidassist.shared.security.library.autoconfigure.SupabaseSecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {SupabaseSecurityAutoConfiguration.class})
public class ConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
