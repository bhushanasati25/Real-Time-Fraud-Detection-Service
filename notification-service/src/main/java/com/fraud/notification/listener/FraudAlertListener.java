package com.fraud.notification.listener;

import com.fraud.common.constants.KafkaConstants;
import com.fraud.common.dto.FraudAlert;
import com.fraud.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Fraud Alert Listener - Consumes fraud alerts from Kafka.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FraudAlertListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaConstants.TOPIC_FRAUD_ALERTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onFraudAlertReceived(
            @Payload FraudAlert alert,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received fraud alert {} for transaction {} from partition {} at offset {}",
                alert.getAlertId(), alert.getTransactionId(), partition, offset);

        try {
            // Process the alert and send notifications
            notificationService.processAlert(alert);

            // Acknowledge successful processing
            acknowledgment.acknowledge();

            log.info("Successfully processed fraud alert {}", alert.getAlertId());

        } catch (Exception e) {
            log.error("Failed to process fraud alert {}: {}",
                    alert.getAlertId(), e.getMessage(), e);
            
            // Acknowledge to prevent infinite reprocessing
            acknowledgment.acknowledge();
        }
    }
}
