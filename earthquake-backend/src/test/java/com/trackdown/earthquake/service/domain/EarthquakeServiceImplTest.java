package com.trackdown.earthquake.service.domain;

import com.trackdown.earthquake.exceptions.EarthquakeNotFoundException;
import com.trackdown.earthquake.exceptions.InvalidGeoJsonException;
import com.trackdown.earthquake.model.Earthquake;
import com.trackdown.earthquake.repository.EarthquakeRepository;
import com.trackdown.earthquake.service.domain.impl.EarthquakeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EarthquakeServiceImplTest {

    private EarthquakeServiceImpl service;

    @BeforeEach
    void setUp() {
        EarthquakeRepository mockRepository = Mockito.mock(EarthquakeRepository.class);
        service = new EarthquakeServiceImpl(mockRepository, JsonMapper.builder().build());
    }

    //Proveruva deka kompleten, validen GeoJSON feature se parsira tocno vo Earthquake so site polinja korektni
    @Test
    void parseFromGeoJson_validFeature_returnsEarthquake() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": 3.5,
                        "magType": "ml",
                        "place": "10km SE of Skopje",
                        "title": "M 3.5 - 10km SE of Skopje",
                        "time": 1750000000000
                      },
                      "geometry": {
                        "coordinates": [21.43, 41.99, 10.0]
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).hasSize(1);
        Earthquake eq = result.get(0);
        assertThat(eq.getMagnitude()).isEqualTo(3.5);
        assertThat(eq.getMagType()).isEqualTo("ml");
        assertThat(eq.getPlace()).isEqualTo("10km SE of Skopje");
        assertThat(eq.getLongitude()).isEqualTo(21.43);
        assertThat(eq.getLatitude()).isEqualTo(41.99);
        assertThat(eq.getDepth()).isEqualTo(10.0);
    }

    //Ako "mag" nedostasuva vo feature-ot, toj zemjotres treba da se preskokne, ne da padne aplikacijata
    @Test
    void parseFromGeoJson_missingMagnitude_skipsFeature() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "place": "somewhere",
                        "time": 1750000000000
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).isEmpty();
    }

    //Isto kako pogore, no za nedostasuvacko "time" pole
    @Test
    void parseFromGeoJson_missingTime_skipsFeature() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": 2.1,
                        "place": "somewhere"
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).isEmpty();
    }

    //Ako "mag" e string namesto broj (nevaliden podatok), se preskoknuva bezbedno namesto da frli greska
    @Test
    void parseFromGeoJson_nonNumericMagnitude_skipsFeature() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": "unknown",
                        "time": 1750000000000
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).isEmpty();
    }

    //Ako "place/title/magType" nedostasuvaat, treba da se postavat na "Unknown" namesto null ili greska
    @Test
    void parseFromGeoJson_missingPlaceAndTitle_fallsBackToUnknown() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": 4.0,
                        "time": 1750000000000
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlace()).isEqualTo("Unknown");
        assertThat(result.get(0).getTitle()).isEqualTo("Unknown");
        assertThat(result.get(0).getMagType()).isEqualTo("Unknown");
    }

    //Ako nema koordinati, zemjotresot sepak se zacuvuva, samo longituda, latituda i depth ostanuvaat null
    @Test
    void parseFromGeoJson_missingGeometry_leavesCoordinatesNull() {
        String json = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": 2.5,
                        "time": 1750000000000
                      }
                    }
                  ]
                }
                """;

        List<Earthquake> result = service.parseFromGeoJson(json);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLongitude()).isNull();
        assertThat(result.get(0).getLatitude()).isNull();
        assertThat(result.get(0).getDepth()).isNull();
    }

    //Ako celoto "features" pole nedostasuva od JSON-ot, treba da frli InvalidGeoJsonException
    @Test
    void parseFromGeoJson_missingFeaturesField_throwsInvalidGeoJsonException() {
        String json = "{ \"type\": \"FeatureCollection\" }";

        assertThatThrownBy(() -> service.parseFromGeoJson(json))
                .isInstanceOf(InvalidGeoJsonException.class);
    }

    //Ako JSON stringot ima losha sintaksa (ne moze voopsto da se parsira), frla InvalidGeoJsonException
    @Test
    void parseFromGeoJson_badJson_throwsInvalidGeoJsonException() {
        String malformed = "{ this is not valid json ";

        assertThatThrownBy(() -> service.parseFromGeoJson(malformed))
                .isInstanceOf(InvalidGeoJsonException.class);
    }

    //Ako korisnikot ne prakja minimalna magnituda (null), filterot treba da gi vrati site zemjotresi
    @Test
    void filterByMagnitude_nullMinMagnitude_returnsAllEarthquakes() {
        List<Earthquake> input = List.of(
                new Earthquake(1.0, "ml", "A", "A title", null, null, null, null),
                new Earthquake(5.0, "ml", "B", "B title", null, null, null, null)
        );

        List<Earthquake> result = service.filterByMagnitude(input, null);
        assertThat(result).hasSize(2);
    }

    //Proveruva deka granicata e strogo "pogolemo od", odnosno zemjotres so tocno ista magnituda kako pragot NE pominuva
    @Test
    void filterByMagnitude_filtersOutEqualAndBelowThreshold() {
        List<Earthquake> input = List.of(
                new Earthquake(2.0, "ml", "A", "A title", null, null, null, null),  // еднакво - отфрлено
                new Earthquake(1.9, "ml", "B", "B title", null, null, null, null),  // помало - отфрлено
                new Earthquake(2.1, "ml", "C", "C title", null, null, null, null)   // поголемо - задржано
        );

        List<Earthquake> result = service.filterByMagnitude(input, 2.0);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlace()).isEqualTo("C");
    }

    //Ako korisnikot ne prakja "after" vreme (null), filterot treba da gi vrati site zemjotresi
    @Test
    void filterByTime_nullAfter_returnsAllEarthquakes() {
        Instant time1 = Instant.parse("2026-01-01T00:00:00Z");
        Instant time2 = Instant.parse("2026-06-01T00:00:00Z");

        List<Earthquake> input = List.of(
                new Earthquake(3.0, "ml", "A", "A title", time1, null, null, null),
                new Earthquake(4.0, "ml", "B", "B title", time2, null, null, null)
        );

        List<Earthquake> result = service.filterByTime(input, null);
        assertThat(result).hasSize(2);
    }

    //Proveruva deka se zadrzuvaat samo zemjotresite koi se sluchile na ili posle zadadenoto vreme "after"
    @Test
    void filterByTime_filtersOutEarthquakesBeforeGivenTime() {
        Instant before = Instant.parse("2026-01-01T00:00:00Z");
        Instant after = Instant.parse("2026-06-01T00:00:00Z");
        Instant filterFrom = Instant.parse("2026-03-01T00:00:00Z");

        List<Earthquake> input = List.of(
                new Earthquake(3.0, "ml", "Before", "Before title", before, null, null, null),
                new Earthquake(4.0, "ml", "After", "After title", after, null, null, null)
        );

        List<Earthquake> result = service.filterByTime(input, filterFrom);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlace()).isEqualTo("After");
    }

    //Ako zemjotresot vo listata nema "time" (null), treba bezbedno da se ignorira, ne da frli greska
    @Test
    void filterByTime_nullTimeOnEarthquake_isSkippedSafely() {
        List<Earthquake> input = List.of(
                new Earthquake(3.0, "ml", "NoTime", "No time title", null, null, null, null)
        );
        List<Earthquake> result = service.filterByTime(input, Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(result).isEmpty();
    }

    //Ako id-to postoi vo bazata, deleteById treba da go povika repository.deleteById bez greska
    @Test
    void deleteById_existingId_deletesSuccessfully() {
        EarthquakeRepository mockRepository = Mockito.mock(EarthquakeRepository.class);
        EarthquakeServiceImpl serviceWithMock = new EarthquakeServiceImpl(mockRepository, JsonMapper.builder().build());
        Mockito.when(mockRepository.existsById(1L)).thenReturn(true);
        serviceWithMock.deleteById(1L);
        Mockito.verify(mockRepository).deleteById(1L);
    }

    //Ako id-to NE postoi vo bazata, treba da frli EarthquakeNotFoundException i da NE go povika deleteById
    @Test
    void deleteById_nonExistingId_throwsEarthquakeNotFoundException() {
        EarthquakeRepository mockRepository = Mockito.mock(EarthquakeRepository.class);
        EarthquakeServiceImpl serviceWithMock = new EarthquakeServiceImpl(mockRepository, JsonMapper.builder().build());
        Mockito.when(mockRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> serviceWithMock.deleteById(999L))
                .isInstanceOf(EarthquakeNotFoundException.class);

        Mockito.verify(mockRepository, Mockito.never()).deleteById(999L);
    }
}