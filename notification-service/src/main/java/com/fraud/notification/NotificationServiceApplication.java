package com.fraud.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Notification Service Application Entry Point.
 * 
 * This service handles sending notifications (email, webhook, SMS)
 * when fraud is detected in transactions.
 */
@SpringBootApplication
@EnableAsync
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
