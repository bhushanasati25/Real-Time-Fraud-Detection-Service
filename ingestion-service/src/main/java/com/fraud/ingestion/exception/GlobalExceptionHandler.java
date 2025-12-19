package com.fraud.ingestion.exception;

import com.fraud.common.dto.ApiResponse;
import com.fraud.common.exception.FraudDetectionException;
import com.fraud.common.exception.MessagePublishException;
import com.fraud.common.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for the Ingestion Service.
 * 
 * Provides consistent error responses across all endpoints.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions from custom validator.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code(ex.getErrorCode())
                .description(String.join("; ", ex.getValidationErrors()))
                .suggestion("Please check your input data and try again")
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                error
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle Bean Validation exceptions.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Bean validation failed: {}", errors);

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .description(String.join("; ", errors))
                .suggestion("Please check your input data and try again")
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                error
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle message publish exceptions.
     */
    @ExceptionHandler(MessagePublishException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessagePublishException(
            MessagePublishException ex) {
        
        log.error("Message publish exception for topic {}: {}", ex.getTopic(), ex.getMessage());

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code(ex.getErrorCode())
                .description("Failed to publish message to processing queue")
                .suggestion("Please try again later or contact support")
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Message queue unavailable",
                error
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Handle malformed JSON.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code("MALFORMED_JSON")
                .description("The request body contains invalid JSON")
                .suggestion("Please verify your JSON syntax and try again")
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request body",
                error
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle generic fraud detection exceptions.
     */
    @ExceptionHandler(FraudDetectionException.class)
    public ResponseEntity<ApiResponse<Object>> handleFraudDetectionException(
            FraudDetectionException ex) {
        
        log.error("Fraud detection exception: {}", ex.getMessage(), ex);

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code(ex.getErrorCode())
                .description(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Processing error",
                error
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);

        ApiResponse.ErrorDetails error = ApiResponse.ErrorDetails.builder()
                .code("INTERNAL_ERROR")
                .description("An unexpected error occurred")
                .suggestion("Please try again later or contact support")
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                error
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
