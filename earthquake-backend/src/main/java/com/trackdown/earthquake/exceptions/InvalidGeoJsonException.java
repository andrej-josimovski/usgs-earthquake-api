package com.trackdown.earthquake.exceptions;

public class InvalidGeoJsonException extends RuntimeException {

    public InvalidGeoJsonException(String message) {
        super("Invalid GeoJSON: " + message);
    }
}
