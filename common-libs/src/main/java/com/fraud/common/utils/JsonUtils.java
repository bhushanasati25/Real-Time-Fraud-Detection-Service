package com.fraud.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

/**
 * JSON Utility class for serialization and deserialization.
 * 
 * Provides a pre-configured ObjectMapper and convenience methods
 * for JSON operations throughout the fraud detection system.
 */
@Slf4j
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get the configured ObjectMapper instance.
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Serialize object to JSON string.
     *
     * @param object The object to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * Serialize object to JSON string, returning empty string on error.
     *
     * @param object The object to serialize
     * @return JSON string or empty string on error
     */
    public static String toJsonSafe(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Deserialize JSON string to object.
     *
     * @param json  The JSON string
     * @param clazz The target class
     * @param <T>   The target type
     * @return Deserialized object
     * @throws IOException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * Deserialize JSON string to object, returning Optional.
     *
     * @param json  The JSON string
     * @param clazz The target class
     * @param <T>   The target type
     * @return Optional containing the deserialized object, or empty on error
     */
    public static <T> Optional<T> fromJsonSafe(String json, Class<T> clazz) {
        try {
            return Optional.of(OBJECT_MAPPER.readValue(json, clazz));
        } catch (IOException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Deserialize JSON string to generic type.
     *
     * @param json          The JSON string
     * @param typeReference The type reference
     * @param <T>           The target type
     * @return Deserialized object
     * @throws IOException if deserialization fails
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    /**
     * Convert object to byte array.
     *
     * @param object The object to serialize
     * @return Byte array representation
     * @throws JsonProcessingException if serialization fails
     */
    public static byte[] toBytes(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    /**
     * Deserialize byte array to object.
     *
     * @param bytes The byte array
     * @param clazz The target class
     * @param <T>   The target type
     * @return Deserialized object
     * @throws IOException if deserialization fails
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(bytes, clazz);
    }

    /**
     * Pretty print JSON.
     *
     * @param json The JSON string
     * @return Pretty formatted JSON string
     */
    public static String prettyPrint(String json) {
        try {
            Object obj = OBJECT_MAPPER.readValue(json, Object.class);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("Failed to pretty print JSON: {}", e.getMessage());
            return json;
        }
    }

    /**
     * Check if string is valid JSON.
     *
     * @param json The string to validate
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
