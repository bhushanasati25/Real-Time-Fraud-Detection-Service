package com.fraud.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration for ML Service.
 * 
 * Configures the reactive WebClient for calling the ML model service.
 */
@Configuration
public class WebClientConfig {

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    @Value("${ml.service.timeout:5000}")
    private int timeout;

    @Bean
    public WebClient mlServiceWebClient() {
        return WebClient.builder()
                .baseUrl(mlServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
