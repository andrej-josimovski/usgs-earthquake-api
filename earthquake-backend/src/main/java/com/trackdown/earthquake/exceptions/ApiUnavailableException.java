package com.trackdown.earthquake.exceptions;

public class ApiUnavailableException extends RuntimeException {

    public ApiUnavailableException() {
        super("USGS API is unavailable. Try again later.");
    }

    public ApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
