package com.assignment.exceptions;

import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;
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
 * Global exception handler for handling and logging exceptions.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.assignment")
public class ExceptionsHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Errors> handleException(Exception ex) {

        HttpStatus status = eveluateExceptionKind(ex);
        log.error("Exception occured when processing the request:"+ex.getMessage());
        Errors errors=new Errors(status.value(), ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);

    }

    private  HttpStatus eveluateExceptionKind(Exception ex)
    {
        if(ex instanceof AccountNotFoundException){
            return HttpStatus.NOT_FOUND;
        }
        else if(ex instanceof ConstraintViolationException){
            return HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof MissingServletRequestParameterException){
            return HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof ConstraintDefinitionException){
            return HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof MethodArgumentNotValidException){
            return HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof MethodArgumentTypeMismatchException){
            return HttpStatus.BAD_REQUEST;
        }
        else {
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
