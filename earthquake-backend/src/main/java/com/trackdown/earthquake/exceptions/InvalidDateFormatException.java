package com.trackdown.earthquake.exceptions;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException() {
        super("Invalid date format. Use: yyyy-MM-ddTHH:mm:ssZ (example: 2026-04-15T10:30:00Z)");
    }
}
