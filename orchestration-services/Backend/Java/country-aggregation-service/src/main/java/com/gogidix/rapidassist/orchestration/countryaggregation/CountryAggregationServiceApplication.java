package com.gogidix.rapidassist.orchestration.countryaggregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@EnableCaching
public class CountryAggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CountryAggregationServiceApplication.class, args);
    }
}
