package com.example.weather_service.web;

import com.example.weather_service.service.WeatherDataService;
import com.example.weather_service.service.dto.AverageDataDto;
import com.example.weather_service.service.dto.WeatherDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherDataController {

    private final WeatherDataService service;

    @GetMapping("/current")
    public WeatherDataDto getCurrent() {
        return service.getLatest();
    }

    @GetMapping
    public AverageDataDto getAverage(@RequestBody String request) throws JsonProcessingException {
        return service.getAverageByPeriod(request);
    }

}
