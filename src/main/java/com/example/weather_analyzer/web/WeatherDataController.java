package com.example.weather_analyzer.web;

import com.example.weather_analyzer.service.WeatherDataService;
import com.example.weather_analyzer.service.dto.AverageDataDto;
import com.example.weather_analyzer.service.dto.WeatherDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherDataController {

    private final WeatherDataService service;

    @GetMapping
    public WeatherDataDto getCurrent() {
        return service.getLatest();
    }

    @GetMapping
    public AverageDataDto getAverage(@RequestParam String from, @RequestParam String to) {
        return service.getAverageByPeriod(from, to);
    }

}
