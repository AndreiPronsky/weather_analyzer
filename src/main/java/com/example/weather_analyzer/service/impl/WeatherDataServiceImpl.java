package com.example.weather_analyzer.service.impl;

import com.example.weather_analyzer.client.WeatherClient;
import com.example.weather_analyzer.data.WeatherDataRepository;
import com.example.weather_analyzer.data.entities.WeatherData;
import com.example.weather_analyzer.service.WeatherDataMapper;
import com.example.weather_analyzer.service.WeatherDataService;
import com.example.weather_analyzer.service.dto.AverageDataDto;
import com.example.weather_analyzer.service.dto.WeatherDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherDataServiceImpl implements WeatherDataService {

    private final WeatherDataMapper mapper;
    private final WeatherDataRepository repository;
    private final WeatherClient client;
    private final ObjectMapper objectMapper;

    @Override
    @Scheduled(fixedRateString = "${TIME_BETWEEN_REQUESTS}")
    public void save() throws JsonProcessingException {
            String response = client.getCurrent();
            WeatherDataDto extracted = objectMapper.readValue(response, WeatherDataDto.class);
            repository.save(mapper.toEntity(extracted));
            log.info("SAVED current {}", extracted);
    }

    @Override
    public WeatherDataDto getLatest() {
        Long lastInsertionId = repository.count();
        return mapper.toDto(repository.findById(lastInsertionId).orElseThrow(NoSuchElementException::new));
    }

    @Override
    public AverageDataDto getAverageByPeriod(String from, String to) {
        List<WeatherData> included = repository.findAllIdBetween(getIdFrom(from), getIdTo(to));
        AverageDataDto result = new AverageDataDto();
        result.setHumidity(getAverageHumidity(included));
        result.setTemperature(countAverage(included.stream()
                .map(WeatherData::getTemperature)
                .collect(Collectors.toList())));
        result.setWindSpeed(countAverage(included.stream()
                .map(WeatherData::getWindSpeed)
                .collect(Collectors.toList())));
        result.setPressure(countAverage(included.stream()
                .map(WeatherData::getPressure)
                .collect(Collectors.toList())));
        return result;
    }

    private int getAverageHumidity(List<WeatherData> includedIntoPeriod) {
        List<Integer> humidityResults = includedIntoPeriod.stream()
                .map(WeatherData::getHumidity)
                .toList();
        return countAverage(humidityResults.stream()
                .map(Double::valueOf)
                .collect(Collectors.toList()))
                .intValue();
    }

    private Double countAverage(List<Double> values) {
        return values.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    private Long getIdFrom(String dateFrom) {
        String[] splitted = dateFrom.split("-");
        String reformatted = String.format("20%s-%s-%s", splitted[2], splitted[1], splitted[0]);
        LocalDate localDate = LocalDate.parse(reformatted);
        return localDate.atStartOfDay()
                .toInstant(ZoneOffset.MIN)
                .toEpochMilli();
    }

    private Long getIdTo(String dateTo) {
        String[] splitted = dateTo.split("-");
        int theDayAfter = Byte.parseByte(splitted[2]) + 1;
        String reformatted = String.format("20%s-%s-%s", splitted[2], splitted[1], theDayAfter);
        LocalDate localDate = LocalDate.parse(reformatted);
        return localDate.atStartOfDay()
                .toInstant(ZoneOffset.MIN)
                .toEpochMilli() - 1;
    }
}
