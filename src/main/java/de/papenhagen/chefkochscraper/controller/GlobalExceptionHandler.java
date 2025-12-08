package de.papenhagen.chefkochscraper.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        logger.debug("Handling IllegalArgumentException: {}", e.getMessage(), e);
        final Map<String, String> response = Map.of(
                "error", "Invalid argument",
                "message", e.getMessage() != null ? e.getMessage() : "Invalid input"
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        logger.debug("Handling MethodArgumentNotValidException: {}", e.getMessage(), e);
        final Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
                ));
        final Map<String, Object> response = Map.of(
                "error", "Validation failed",
                "message", "Request validation failed",
                "fieldErrors", errors
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        final String requiredType = e.getRequiredType() == null ? "unknown" : e.getRequiredType().getSimpleName();
        final Map<String, Object> response = Map.of(
                "error", "Invalid Request Parameter",
                "message", String.format("Parameter '%s' must be of type '%s'.", e.getName(), requiredType)
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        logger.debug("Handling MissingServletRequestParameterException: {}", e.getMessage(), e);
        final Map<String, String> response = Map.of(
                "error", "Missing parameter",
                "message", String.format("Required parameter '%s' is missing", e.getParameterName())
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFound(NoHandlerFoundException e) {
        logger.debug("Handling NoHandlerFoundException: {}", e.getMessage(), e);
        Map<String, String> response = Map.of(
                "error", "Not Found",
                "message", String.format("The requested endpoint '%s' was not found", e.getRequestURL())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        logger.debug("Handling HttpRequestMethodNotSupportedException: {}", e.getMessage());
        String supportedMethods = "Unknown";
        if (e.getSupportedMethods() != null) {
            supportedMethods = String.join(", ", e.getSupportedMethods());
        }
        Map<String, String> response = Map.of(
                "error", "Method Not Allowed",
                "message", String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s", e.getMethod(), supportedMethods));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        logger.debug("Handling HttpMediaTypeNotSupportedException: {}", e.getMessage());
        Map<String, String> response = Map.of(
                "error", "Unsupported Media Type",
                "message", e.getMessage() != null ? e.getMessage() : "Content type not supported");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException e) {
        logger.debug("Handling NoResourceFoundException: {}", e.getMessage());
        Map<String, String> response = Map.of(
                "error", "Resource Not Found",
                "message", "The requested resource was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFileNotFound(FileNotFoundException e) {
        logger.debug("Handling FileNotFoundException: {}", e.getMessage());
        Map<String, String> response = Map.of(
                "error", "File Not Found",
                "message", "The requested file was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        logger.debug("Handling AccessDeniedException: {}", e.getMessage());
        Map<String, String> response = Map.of(
                "error", "Access Denied",
                "message", "Access to the requested resource is denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Handling unexpected exception: {}", e.getMessage(), e);
        Map<String, String> response = Map.of(
                "error", "An error occurred.",
                "message", e.getMessage() != null ? e.getMessage() : "");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
