package com.assignment.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents error details, including status, message, and timestamp.
 */
@Data
@AllArgsConstructor
public class Errors {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
}