package com.gogidix.rapidassist.orchestration.dispatching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableKafka
@EnableScheduling
public class DispatchingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DispatchingServiceApplication.class, args);
    }
}
