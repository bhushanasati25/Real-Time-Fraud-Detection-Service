package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * ApiResponse DTO - Standard API response wrapper.
 * 
 * Provides consistent response format across all API endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Whether the request was successful
     */
    @JsonProperty("success")
    private boolean success;

    /**
     * HTTP status code
     */
    @JsonProperty("statusCode")
    private int statusCode;

    /**
     * Response message
     */
    @JsonProperty("message")
    private String message;

    /**
     * Response data payload
     */
    @JsonProperty("data")
    private T data;

    /**
     * Trace ID for request tracking
     */
    @JsonProperty("traceId")
    private String traceId;

    /**
     * Response timestamp
     */
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    /**
     * Error details (if any)
     */
    @JsonProperty("error")
    private ErrorDetails error;

    /**
     * Static factory for success response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Static factory for created response
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(201)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Static factory for accepted response
     */
    public static <T> ApiResponse<T> accepted(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(202)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Static factory for error response
     */
    public static <T> ApiResponse<T> error(int statusCode, String message, ErrorDetails error) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Error details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("code")
        private String code;

        @JsonProperty("description")
        private String description;

        @JsonProperty("field")
        private String field;

        @JsonProperty("suggestion")
        private String suggestion;
    }
}
