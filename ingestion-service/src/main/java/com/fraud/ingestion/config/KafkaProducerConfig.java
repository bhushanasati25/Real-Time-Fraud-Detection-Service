package com.fraud.ingestion.config;

import com.fraud.common.dto.TransactionEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration.
 * 
 * Configures the Kafka producer with JSON serialization for
 * TransactionEvent objects.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String acks;

    @Value("${spring.kafka.producer.retries:3}")
    private int retries;

    @Value("${spring.kafka.producer.batch-size:16384}")
    private int batchSize;

    @Value("${spring.kafka.producer.linger-ms:5}")
    private int lingerMs;

    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private long bufferMemory;

    /**
     * Producer configuration map.
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        
        // Bootstrap servers
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Serializers
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Reliability settings
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        // Performance settings
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        
        // Compression
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        
        // Client ID
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "ingestion-service-producer");
        
        return props;
    }

    /**
     * Producer factory for TransactionEvent.
     */
    @Bean
    public ProducerFactory<String, TransactionEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template for sending TransactionEvent messages.
     */
    @Bean
    public KafkaTemplate<String, TransactionEvent> kafkaTemplate() {
        KafkaTemplate<String, TransactionEvent> template = new KafkaTemplate<>(producerFactory());
        template.setObservationEnabled(true);
        return template;
    }

    /**
     * Generic producer factory for any object type.
     */
    @Bean
    public ProducerFactory<String, Object> genericProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Generic Kafka template for sending any object.
     */
    @Bean
    public KafkaTemplate<String, Object> genericKafkaTemplate() {
        return new KafkaTemplate<>(genericProducerFactory());
    }
}
