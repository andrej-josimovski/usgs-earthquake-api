package com.trackdown.earthquake.dto;

import com.trackdown.earthquake.model.Earthquake;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record DisplayEarthquakeDto(
        Long id,
        Double magnitude,
        String magType,
        String place,
        String title,
        Instant time,
        Double longitude,
        Double latitude,
        Double depth
) {
    public static DisplayEarthquakeDto from(Earthquake earthquake) {
        return new DisplayEarthquakeDto(
                earthquake.getId(),
                earthquake.getMagnitude(),
                earthquake.getMagType(),
                earthquake.getPlace(),
                earthquake.getTitle(),
                earthquake.getTime(),
                earthquake.getLongitude(),
                earthquake.getLatitude(),
                earthquake.getDepth()
        );
    }

    public static List<DisplayEarthquakeDto> from(List<Earthquake> earthquakes) {
        return earthquakes.stream()
                .map(DisplayEarthquakeDto::from)
                .collect(Collectors.toList());
    }
}
