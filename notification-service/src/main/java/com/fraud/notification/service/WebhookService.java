package com.fraud.notification.service;

import com.fraud.common.dto.FraudAlert;
import com.fraud.common.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Webhook Service - Sends fraud alerts to external webhook endpoints.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebClient.Builder webClientBuilder;

    @Value("${notification.webhook.url:}")
    private String webhookUrl;

    @Value("${notification.webhook.enabled:false}")
    private boolean webhookEnabled;

    @Value("${notification.webhook.timeout:5000}")
    private int timeout;

    /**
     * Send alert to webhook endpoint.
     *
     * @param alert The fraud alert
     */
    @Async
    public void sendWebhook(FraudAlert alert) {
        if (!webhookEnabled || webhookUrl == null || webhookUrl.isBlank()) {
            log.info("Webhook notifications disabled. Alert {} would be sent to webhook",
                    alert.getAlertId());
            log.debug("Alert payload: {}", JsonUtils.toJsonSafe(alert));
            return;
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(webhookUrl).build();

            webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(alert)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .doOnSuccess(response -> 
                            log.info("Webhook sent successfully for alert {}", alert.getAlertId()))
                    .doOnError(error -> 
                            log.error("Webhook failed for alert {}: {}", alert.getAlertId(), error.getMessage()))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to send webhook for alert {}: {}", alert.getAlertId(), e.getMessage());
        }
    }

    /**
     * Send alert to a specific webhook URL.
     *
     * @param alert      The fraud alert
     * @param targetUrl  The webhook URL
     */
    public void sendWebhookToUrl(FraudAlert alert, String targetUrl) {
        if (targetUrl == null || targetUrl.isBlank()) {
            log.warn("No webhook URL provided for alert {}", alert.getAlertId());
            return;
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(targetUrl).build();

            webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(alert)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            log.info("Webhook sent successfully to {} for alert {}", targetUrl, alert.getAlertId());

        } catch (Exception e) {
            log.error("Failed to send webhook to {} for alert {}: {}", 
                    targetUrl, alert.getAlertId(), e.getMessage());
        }
    }
}
