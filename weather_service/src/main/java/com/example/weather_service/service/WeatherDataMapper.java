package com.example.weather_service.service;

import com.example.weather_service.data.entities.WeatherData;
import com.example.weather_service.service.dto.WeatherDataDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherDataMapper {

    WeatherDataDto toDto(WeatherData entity);

    WeatherData toEntity(WeatherDataDto dto);
}
