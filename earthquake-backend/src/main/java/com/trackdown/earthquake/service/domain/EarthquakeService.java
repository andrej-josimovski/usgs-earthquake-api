package com.trackdown.earthquake.service.domain;

import com.trackdown.earthquake.model.Earthquake;

import java.time.Instant;
import java.util.List;

public interface EarthquakeService {
    List<Earthquake> parseFromGeoJson(String rawJson);
    List<Earthquake> filterByMagnitude(List<Earthquake> earthquakes, Double magnitude);
    List<Earthquake> filterByTime(List<Earthquake> earthquakes, Instant after);
    List<Earthquake> replaceAll(List<Earthquake> newData);
    List<Earthquake> getAll();
    List<Earthquake> getFiltered(Double minMagnitude, String after);
    void deleteById(Long id);
}
