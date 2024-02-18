package com.example.weather_service.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataDto {

    private Long id;

    @Positive
    private Long epochTime;

    @NotNull
    private Double temperature;

    @Positive
    private Double windSpeed;

    @Positive
    private Double pressure;

    @Positive
    private Integer humidity;

    @NotNull
    private String conditions;

    @NotNull
    private String location;
}
