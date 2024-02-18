package com.example.weather_service.service;

import com.example.weather_service.service.dto.AverageDataDto;
import com.example.weather_service.service.dto.WeatherDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface WeatherDataService {

    void save();

    WeatherDataDto getLatest();

    AverageDataDto getAverageByPeriod(String request) throws JsonProcessingException;

}
