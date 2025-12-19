package com.fraud.notification.service;

import com.fraud.common.dto.FraudAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Notification Service - Orchestrates sending notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final WebhookService webhookService;

    /**
     * Process a fraud alert and send notifications.
     *
     * @param alert The fraud alert to process
     */
    public void processAlert(FraudAlert alert) {
        log.info("Processing alert {} with severity {}", alert.getAlertId(), alert.getSeverity());

        if (alert.getNotificationChannels() == null || alert.getNotificationChannels().isEmpty()) {
            log.warn("No notification channels specified for alert {}", alert.getAlertId());
            sendDefaultNotifications(alert);
            return;
        }

        for (FraudAlert.NotificationChannel channel : alert.getNotificationChannels()) {
            try {
                switch (channel) {
                    case EMAIL -> emailService.sendAlertEmail(alert);
                    case WEBHOOK -> webhookService.sendWebhook(alert);
                    case SLACK -> sendSlackNotification(alert);
                    case SMS -> sendSmsNotification(alert);
                    case PUSH_NOTIFICATION -> sendPushNotification(alert);
                }
            } catch (Exception e) {
                log.error("Failed to send {} notification for alert {}: {}",
                        channel, alert.getAlertId(), e.getMessage());
            }
        }
    }

    private void sendDefaultNotifications(FraudAlert alert) {
        // Default: send email and webhook
        try {
            emailService.sendAlertEmail(alert);
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }

        try {
            webhookService.sendWebhook(alert);
        } catch (Exception e) {
            log.error("Failed to send webhook notification: {}", e.getMessage());
        }
    }

    @Async
    protected void sendSlackNotification(FraudAlert alert) {
        // Placeholder for Slack integration
        log.info("Sending Slack notification for alert {}", alert.getAlertId());
        // In production, integrate with Slack API
    }

    @Async
    protected void sendSmsNotification(FraudAlert alert) {
        // Placeholder for SMS integration
        log.info("Sending SMS notification for alert {}", alert.getAlertId());
        // In production, integrate with Twilio or similar
    }

    @Async
    protected void sendPushNotification(FraudAlert alert) {
        // Placeholder for push notification
        log.info("Sending push notification for alert {}", alert.getAlertId());
        // In production, integrate with Firebase Cloud Messaging
    }
}
