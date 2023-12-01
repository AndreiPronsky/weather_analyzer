package com.example.weather_analyzer.service;

import com.example.weather_analyzer.data.entities.WeatherData;
import com.example.weather_analyzer.service.dto.WeatherDataDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherDataMapper {

    WeatherDataDto toDto(WeatherData entity);

    WeatherData toEntity(WeatherDataDto dto);
}
