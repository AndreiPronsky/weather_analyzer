package com.example.weather_service.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "epoch_time")
    private Long epochTime;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "pressure")
    private Double pressure;

    @Column(name = "humidity")
    private Integer humidity;

    @Column(name = "conditions")
    private String conditions;

    @Column(name = "location")
    private String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Objects.equals(id, that.id) && Objects.equals(temperature, that.temperature) && Objects.equals(windSpeed, that.windSpeed) && Objects.equals(pressure, that.pressure) && Objects.equals(humidity, that.humidity) && Objects.equals(conditions, that.conditions) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, temperature, windSpeed, pressure, humidity, conditions, location);
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", temperature=" + temperature +
                ", wind_speed=" + windSpeed +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", conditions='" + conditions + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
