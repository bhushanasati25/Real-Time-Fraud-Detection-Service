package com.fraud.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Fraud Engine Application Entry Point.
 * 
 * This service is the core processing unit that consumes transactions
 * from Kafka, applies fraud detection rules and ML scoring, and
 * publishes alerts for fraudulent transactions.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class FraudEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudEngineApplication.class, args);
    }
}
