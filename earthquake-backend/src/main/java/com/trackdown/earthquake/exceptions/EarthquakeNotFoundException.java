package com.trackdown.earthquake.exceptions;

public class EarthquakeNotFoundException extends RuntimeException {
    public EarthquakeNotFoundException(Long id) {
        super("Earthquake not found with id: " + id);
    }
}
