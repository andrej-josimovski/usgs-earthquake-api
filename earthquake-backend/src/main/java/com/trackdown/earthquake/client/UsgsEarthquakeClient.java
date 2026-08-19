package com.trackdown.earthquake.client;

import com.trackdown.earthquake.exceptions.ApiUnavailableException;
import com.trackdown.earthquake.exceptions.InvalidGeoJsonException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UsgsEarthquakeClient {

    private static final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private final RestTemplate restTemplate;

    public UsgsEarthquakeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchRaw() {
        String response;
        try {
            response = restTemplate.getForObject(USGS_URL, String.class);
        } catch (RestClientException e) {
            throw new ApiUnavailableException("USGS API is unavailable. Try again later.", e);
        }

        if (response == null || response.isBlank()) {
            throw new InvalidGeoJsonException("USGS API returned an empty response.");
        }
        return response;
    }
}