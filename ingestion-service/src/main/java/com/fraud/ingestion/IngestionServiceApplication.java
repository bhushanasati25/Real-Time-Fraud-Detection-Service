package com.fraud.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Ingestion Service Application Entry Point.
 * 
 * This service provides REST API endpoints for transaction ingestion
 * and publishes events to Kafka for fraud detection processing.
 */
@SpringBootApplication
@EnableAsync
public class IngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionServiceApplication.class, args);
    }
}
