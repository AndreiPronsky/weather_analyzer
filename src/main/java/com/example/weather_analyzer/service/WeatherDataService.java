package com.example.weather_analyzer.service;

import com.example.weather_analyzer.service.dto.AverageDataDto;
import com.example.weather_analyzer.service.dto.WeatherDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface WeatherDataService {

    void save(WeatherDataDto dto) throws JsonProcessingException, InterruptedException;

    WeatherDataDto getLatest();

    AverageDataDto getAverageByPeriod(String from, String to);

}
