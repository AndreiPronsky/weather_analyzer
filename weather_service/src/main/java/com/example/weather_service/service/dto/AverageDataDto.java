package com.example.weather_service.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageDataDto {

    private Double temperature;

    private Double windSpeed;

    private Double pressure;

    private Integer humidity;

    private String conditions;
}
