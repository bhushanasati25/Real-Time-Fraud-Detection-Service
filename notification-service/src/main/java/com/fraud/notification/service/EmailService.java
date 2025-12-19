package com.fraud.notification.service;

import com.fraud.common.dto.FraudAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email Service - Handles sending email notifications for fraud alerts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:fraud-alerts@example.com}")
    private String fromAddress;

    @Value("${notification.email.to:fraud-team@example.com}")
    private String toAddress;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Send an alert email.
     *
     * @param alert The fraud alert
     */
    @Async
    public void sendAlertEmail(FraudAlert alert) {
        if (!emailEnabled) {
            log.info("Email notifications disabled. Alert {}: Transaction {} flagged as fraud",
                    alert.getAlertId(), alert.getTransactionId());
            logAlertDetails(alert);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toAddress);
            message.setSubject(buildSubject(alert));
            message.setText(buildBody(alert));

            mailSender.send(message);
            log.info("Email sent for alert {}", alert.getAlertId());

        } catch (Exception e) {
            log.error("Failed to send email for alert {}: {}", alert.getAlertId(), e.getMessage());
        }
    }

    private String buildSubject(FraudAlert alert) {
        return String.format("[%s] Fraud Alert: %s - Transaction %s",
                alert.getSeverity(),
                alert.getAlertType(),
                alert.getTransactionId());
    }

    private String buildBody(FraudAlert alert) {
        StringBuilder body = new StringBuilder();
        body.append("===========================================\n");
        body.append("       FRAUD DETECTION ALERT\n");
        body.append("===========================================\n\n");
        
        body.append("Alert ID: ").append(alert.getAlertId()).append("\n");
        body.append("Severity: ").append(alert.getSeverity()).append("\n");
        body.append("Alert Type: ").append(alert.getAlertType()).append("\n\n");
        
        body.append("--- Transaction Details ---\n");
        body.append("Transaction ID: ").append(alert.getTransactionId()).append("\n");
        body.append("User ID: ").append(alert.getUserId()).append("\n");
        body.append("Amount: ").append(alert.getCurrency()).append(" ").append(alert.getAmount()).append("\n");
        body.append("Merchant: ").append(alert.getMerchantName()).append("\n");
        body.append("Location: ").append(alert.getLocation()).append("\n");
        body.append("IP Address: ").append(alert.getIpAddress()).append("\n");
        body.append("Transaction Time: ").append(alert.getTransactionTimestamp()).append("\n\n");
        
        body.append("--- Fraud Analysis ---\n");
        body.append("Fraud Score: ").append(alert.getFraudScore()).append("\n");
        body.append("Triggered Rules: ").append(String.join(", ", alert.getTriggeredRules())).append("\n");
        body.append("Description: ").append(alert.getDescription()).append("\n");
        body.append("Recommended Action: ").append(alert.getRecommendedAction()).append("\n\n");
        
        body.append("--- Alert Status ---\n");
        body.append("Status: ").append(alert.getStatus()).append("\n");
        body.append("Alert Time: ").append(alert.getAlertTimestamp()).append("\n\n");
        
        body.append("===========================================\n");
        body.append("This is an automated alert from the Fraud Detection System.\n");
        body.append("Please review and take appropriate action.\n");
        
        return body.toString();
    }

    private void logAlertDetails(FraudAlert alert) {
        log.info("========================================");
        log.info("FRAUD ALERT NOTIFICATION");
        log.info("========================================");
        log.info("Alert ID: {}", alert.getAlertId());
        log.info("Transaction ID: {}", alert.getTransactionId());
        log.info("User ID: {}", alert.getUserId());
        log.info("Amount: {} {}", alert.getCurrency(), alert.getAmount());
        log.info("Severity: {}", alert.getSeverity());
        log.info("Alert Type: {}", alert.getAlertType());
        log.info("Fraud Score: {}", alert.getFraudScore());
        log.info("Triggered Rules: {}", alert.getTriggeredRules());
        log.info("Description: {}", alert.getDescription());
        log.info("Recommended Action: {}", alert.getRecommendedAction());
        log.info("========================================");
    }
}
