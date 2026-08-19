package com.trackdown.earthquake.service.domain.impl;

import com.trackdown.earthquake.model.Earthquake;
import com.trackdown.earthquake.exceptions.DatabaseException;
import com.trackdown.earthquake.exceptions.EarthquakeNotFoundException;
import com.trackdown.earthquake.exceptions.InvalidDateFormatException;
import com.trackdown.earthquake.exceptions.InvalidGeoJsonException;
import com.trackdown.earthquake.repository.EarthquakeRepository;
import com.trackdown.earthquake.service.domain.EarthquakeService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EarthquakeServiceImpl implements EarthquakeService {

    private final EarthquakeRepository earthquakeRepository;
    private final ObjectMapper objectMapper;

    public EarthquakeServiceImpl(EarthquakeRepository earthquakeRepository, ObjectMapper objectMapper) {
        this.earthquakeRepository = earthquakeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Earthquake> parseFromGeoJson(String rawJson) {
        JsonNode features;
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            features = root.get("features");
        } catch (Exception e) {
            throw new InvalidGeoJsonException("Failed to parse GeoJSON response.");
        }

        if (features == null || !features.isArray()) {
            throw new InvalidGeoJsonException("'features' field is missing or not an array.");
        }

        List<Earthquake> result = new ArrayList<>();
        for (JsonNode feature : features) {
            parseFeature(feature).ifPresent(result::add);
        }
        return result;
    }

    private Optional<Earthquake> parseFeature(JsonNode feature) {
        JsonNode props = feature.get("properties");
        if (props == null) return java.util.Optional.empty();

        JsonNode magNode = props.get("mag");
        JsonNode timeNode = props.get("time");
        if (magNode == null || magNode.isNull() || !magNode.isNumber()) return java.util.Optional.empty();
        if (timeNode == null || timeNode.isNull() || !timeNode.isNumber()) return java.util.Optional.empty();

        String place = textOrDefault(props.get("place"), "Unknown");
        String title = textOrDefault(props.get("title"), "Unknown");
        String magType = textOrDefault(props.get("magType"), "Unknown");
        Instant time = Instant.ofEpochMilli(timeNode.asLong());

        JsonNode geometry = feature.get("geometry");
        JsonNode coordinates = geometry != null ? geometry.get("coordinates") : null;

        Double longitude = null, latitude = null, depth = null;
        if (coordinates != null && coordinates.isArray() && coordinates.size() >= 3) {
            longitude = coordinates.get(0).asDouble();
            latitude = coordinates.get(1).asDouble();
            depth = coordinates.get(2).asDouble();
        }

        return java.util.Optional.of(new Earthquake(
                magNode.asDouble(), magType, place, title, time, longitude, latitude, depth));

    }

    private String textOrDefault(JsonNode node, String fallback) {
        return (node != null && !node.isNull()) ? node.asText() : fallback;
    }

    @Override
    public List<Earthquake> filterByMagnitude(List<Earthquake> earthquakes, Double minMagnitude) {
        if (minMagnitude == null) return earthquakes;
        return earthquakes.stream()
                .filter((e -> e.getMagnitude() != null && e.getMagnitude() > minMagnitude))
                .collect(Collectors.toList());
    }

    @Override
    public List<Earthquake> filterByTime(List<Earthquake> earthquakes, Instant after) {
        if (after == null) return earthquakes;
        return earthquakes.stream()
                .filter(e -> e.getTime() != null && !e.getTime().isBefore(after))
                .collect(Collectors.toList());
    }

    @Override
    public List<Earthquake> replaceAll(List<Earthquake> newData) {
        try {
            earthquakeRepository.deleteAll();
            return earthquakeRepository.saveAll(newData);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to save earthquake data to the db", e);
        }
    }

    @Override
    public List<Earthquake> getAll() {
        return earthquakeRepository.findAll();
    }

    @Override
    public List<Earthquake> getFiltered(Double minMagnitude, String after) {
        Instant afterInstant = null;
        if (after != null) {
            try {
                afterInstant = Instant.parse(after);
            } catch (Exception e) {
                throw new InvalidDateFormatException();
            }
        }

        List<Earthquake> all = earthquakeRepository.findAll();
        List<Earthquake> byMagnitude = filterByMagnitude(all, minMagnitude);
        return filterByTime(byMagnitude, afterInstant);
    }

    @Override
    public void deleteById(Long id) {
        if (!earthquakeRepository.existsById(id)){
            throw new EarthquakeNotFoundException(id);
        }
        earthquakeRepository.deleteById(id);
    }
}
