package com.trackdown.earthquake.service.application;

import com.trackdown.earthquake.client.UsgsEarthquakeClient;
import com.trackdown.earthquake.dto.DisplayEarthquakeDto;
import com.trackdown.earthquake.repository.EarthquakeRepository;
import com.trackdown.earthquake.service.application.impl.EarthquakeApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EarthquakeApplicationServiceImplTest {

    @Autowired
    private EarthquakeApplicationServiceImpl applicationService;

    @Autowired
    private EarthquakeRepository repository;

    @MockitoBean
    private UsgsEarthquakeClient client;

    @Test
    void fetchAndStore_parsesAndPersistsEarthquakes() {
        String fakeUsgsResponse = """
                {
                  "features": [
                    {
                      "properties": { "mag": 3.2, "magType": "ml", "place": "Test Place", "title": "M 3.2 - Test", "time": 1750000000000 },
                      "geometry": { "coordinates": [21.43, 41.99, 10.0] }
                    }
                  ]
                }
                """;

        //koga client.fetchRaw() ke se povika, go vraka nasiot fakeJson, nema vistinski HTTP povik
        org.mockito.Mockito.when(client.fetchRaw()).thenReturn(fakeUsgsResponse);
        List<DisplayEarthquakeDto> result = applicationService.fetchAndStore();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).place()).isEqualTo("Test Place");
        //proveruvame deka navistina e zachuvano vo (H2) bazata, ne samo vrateno
        assertThat(repository.count()).isEqualTo(1);
    }

    //Proveruva deka fetchAndStore gi brise starite podatoci pred da gi dodade novite
    @Test
    void fetchAndStore_replacesExistingData() {
        //prvo zacuvuvame eden "star"
        org.mockito.Mockito.when(client.fetchRaw()).thenReturn("""
            {
              "features": [
                { "properties": { "mag": 1.0, "time": 1600000000000 } }
              ]
            }
            """);
        applicationService.fetchAndStore();
        assertThat(repository.count()).isEqualTo(1);
        //potoa povikuvame povtorno so novi dva
        org.mockito.Mockito.when(client.fetchRaw()).thenReturn("""
            {
              "features": [
                { "properties": { "mag": 2.0, "time": 1700000000000 } },
                { "properties": { "mag": 3.0, "time": 1750000000000 } }
              ]
            }
            """);
        applicationService.fetchAndStore();

        //stariot treba da e izbrishan odnosno da ima samo 2 (novite)
        assertThat(repository.count()).isEqualTo(2);
    }

    // Proveruva deka getFiltered navistina filtrira vo bazata spored minMagnitude
    @Test
    void getFiltered_filtersStoredDataByMagnitude() {
        org.mockito.Mockito.when(client.fetchRaw()).thenReturn("""
            {
              "features": [
                { "properties": { "mag": 1.5, "time": 1750000000000 } },
                { "properties": { "mag": 4.5, "time": 1750000000000 } }
              ]
            }
            """);
        applicationService.fetchAndStore();
        List<DisplayEarthquakeDto> result = applicationService.getFiltered(2.0, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).magnitude()).isEqualTo(4.5);
    }
}
