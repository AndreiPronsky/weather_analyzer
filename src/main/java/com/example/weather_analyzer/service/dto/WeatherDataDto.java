package com.example.weather_analyzer.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeatherDataDto {

    @JsonProperty("last_updated_epoch")
    private Long id;

    @JsonProperty("temp_c")
    private Double temperature;

    @JsonProperty("wind_kph")
    private Double windSpeed;

    @JsonProperty("pressure_mb")
    private Double pressure;

    @JsonProperty("humidity")
    private Integer humidity;

    @JsonProperty("condition:text")
    private String conditions;

    @JsonProperty("location:name")
    private String location;
}
