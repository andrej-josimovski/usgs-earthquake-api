package com.trackdown.earthquake.service.application.impl;

import com.trackdown.earthquake.client.UsgsEarthquakeClient;
import com.trackdown.earthquake.dto.DisplayEarthquakeDto;
import com.trackdown.earthquake.model.Earthquake;
import com.trackdown.earthquake.service.application.EarthquakeApplicationService;
import com.trackdown.earthquake.service.domain.EarthquakeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EarthquakeApplicationServiceImpl implements EarthquakeApplicationService {

    private final UsgsEarthquakeClient client;
    private final EarthquakeService earthquakeService;

    public EarthquakeApplicationServiceImpl(UsgsEarthquakeClient client, EarthquakeService earthquakeService) {
        this.client = client;
        this.earthquakeService = earthquakeService;
    }

    @Override
    public List<DisplayEarthquakeDto> fetchAndStore() {
        String rawJson = client.fetchRaw();
        List<Earthquake> parsedData = earthquakeService.parseFromGeoJson(rawJson);
        List<Earthquake> saved = earthquakeService.replaceAll(parsedData);
        return DisplayEarthquakeDto.from(saved);
    }

    @Override
    public List<DisplayEarthquakeDto> getAll() {
        return DisplayEarthquakeDto.from(earthquakeService.getAll());
    }

    @Override
    public List<DisplayEarthquakeDto> getFiltered(Double minMagnitude, String after) {
        return DisplayEarthquakeDto.from(earthquakeService.getFiltered(minMagnitude, after));
    }

    @Override
    public void deleteById(Long id) {
        earthquakeService.deleteById(id);
    }
}
