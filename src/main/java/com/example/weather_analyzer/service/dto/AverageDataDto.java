package com.example.weather_analyzer.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AverageDataDto {

    private Double temperature;

    private Double windSpeed;

    private Double pressure;

    private Integer humidity;
}
