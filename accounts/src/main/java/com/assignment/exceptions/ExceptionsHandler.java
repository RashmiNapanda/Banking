package com.assignment.exceptions;

import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Global exception handler to manage and return appropriate error responses.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.assignment")
public class ExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Errors> handleException(Exception ex) {
        HttpStatus status = evaluateExceptionKind(ex);
        log.error("Exception occurred while processing the request: {}", ex.getMessage(), ex);
        Errors errors = new Errors(status.value(), ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }

    private HttpStatus evaluateExceptionKind(Exception ex) {
        if (ex instanceof TransactionException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof URINotFoundException || ex instanceof UserNotFoundException || ex instanceof AccountNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof ConstraintViolationException ||
                ex instanceof MissingServletRequestParameterException ||
                ex instanceof ConstraintDefinitionException ||
                ex instanceof MethodArgumentNotValidException ||
                ex instanceof MethodArgumentTypeMismatchException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        } else if (ex instanceof NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof DataAccessException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof ResponseStatusException) {
            return (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Errors> handleDatabaseException(DataAccessException ex) {
        log.error("Database error: {}", ex.getMessage(), ex);
        Errors errors = new Errors(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Errors> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("HTTP method not supported: {}", ex.getMessage(), ex);
        Errors errors = new Errors(HttpStatus.METHOD_NOT_ALLOWED.value(), "HTTP method not allowed", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errors);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Errors> handleNoSuchElement(NoSuchElementException ex) {
        log.error("Requested resource not found: {}", ex.getMessage(), ex);
        Errors errors = new Errors(HttpStatus.NOT_FOUND.value(), "Resource not found", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Errors> handleResponseStatus(ResponseStatusException ex) {
        log.error("Response status error: {}", ex.getMessage(), ex);
        Errors errors = new Errors(ex.getStatusCode().value(), ex.getReason(), LocalDateTime.now());
        return ResponseEntity.status(ex.getStatusCode()).body(errors);
    }
}
