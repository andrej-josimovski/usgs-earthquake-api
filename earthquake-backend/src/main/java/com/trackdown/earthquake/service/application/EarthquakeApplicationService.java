package com.trackdown.earthquake.service.application;

import com.trackdown.earthquake.dto.DisplayEarthquakeDto;

import java.util.List;

public interface EarthquakeApplicationService {
    List<DisplayEarthquakeDto> fetchAndStore();
    List<DisplayEarthquakeDto> getAll();
    List<DisplayEarthquakeDto> getFiltered(Double minMagnitude, String after);
    void deleteById(Long id);
}
