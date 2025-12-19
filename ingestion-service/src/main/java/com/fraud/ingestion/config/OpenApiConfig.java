package com.fraud.ingestion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger Configuration.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fraud Detection - Ingestion Service API")
                        .version("1.0.0")
                        .description("""
                                REST API for ingesting financial transactions into the 
                                fraud detection pipeline. Transactions are validated and 
                                published to Kafka for real-time fraud analysis.
                                """)
                        .contact(new Contact()
                                .name("Fraud Detection Team")
                                .email("fraud-detection@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("http://ingestion-service:8080")
                                .description("Docker Environment")
                ));
    }
}
