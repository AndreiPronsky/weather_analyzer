package com.example.weather_service.client.impl;

import com.example.weather_service.client.WeatherClient;
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

    @Value("${CLIENT_URL}")
    private String url;

    @Value("${CLIENT_KEY}")
    private String key;

    @Value("${CLIENT_HOST}")
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
