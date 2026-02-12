package com.gogidix.rapidassist.ai.summarization.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {
        "com.gogidix.rapidassist.ai.summarization",
        "com.gogidix.rapidassist.shared"
})
@EnableMongoRepositories(basePackages = {
        "com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository"
})
@EnableKafka
public class SummarizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummarizationServiceApplication.class, args);
    }
}
