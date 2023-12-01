package com.example.weather_analyzer.client.impl;

import com.example.weather_analyzer.client.WeatherClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherClientImpl implements WeatherClient {

    private final WebClient webClient;

    @Value("https://weatherapi-com.p.rapidapi.com/current.json?q=Minsk")
    private String url;

    @Value("6fe40fb762mshd754f02393d488ep134ff5jsn16f584ba1545")
    private String key;

    @Value("weatherapi-com.p.rapidapi.com")
    private String host;

    @Override
    public String getCurrent() {
        return webClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", key)
                .header("X-RapidAPI-Host", host)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
