package com.trackdown.earthquake.controller;

import com.trackdown.earthquake.dto.DisplayEarthquakeDto;
import com.trackdown.earthquake.service.application.EarthquakeApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakeController {

    private final EarthquakeApplicationService earthquakeApplicationService;

    public EarthquakeController(EarthquakeApplicationService earthquakeApplicationService) {
        this.earthquakeApplicationService = earthquakeApplicationService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<List<DisplayEarthquakeDto>> fetchAndStore(){
        List<DisplayEarthquakeDto> result = earthquakeApplicationService.fetchAndStore();
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<DisplayEarthquakeDto>> getAll(){
        return ResponseEntity.ok(earthquakeApplicationService.getAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<DisplayEarthquakeDto>> getFiltered(
            @RequestParam(required = false) Double minMagnitude,
            @RequestParam(required = false) String after) {
        List<DisplayEarthquakeDto> result = earthquakeApplicationService.getFiltered(minMagnitude, after);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        earthquakeApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
