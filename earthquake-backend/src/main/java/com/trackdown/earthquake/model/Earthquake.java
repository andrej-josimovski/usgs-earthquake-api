package com.trackdown.earthquake.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "earthquakes")
@Getter
@Setter
public class Earthquake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double magnitude;
    private String magType;
    private String place;
    private String title;
    private Instant time;
    private Double longitude;
    private Double latitude;
    private Double depth;

    public Earthquake(Double magnitude, String magType, String place, String title,
                      Instant time, Double longitude, Double latitude, Double depth) {
        this.magnitude = magnitude;
        this.magType = magType;
        this.place = place;
        this.title = title;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
    }

    public Earthquake() {

    }
}
