package com.example.weather_service.service.impl;

import com.example.weather_service.client.WeatherClient;
import com.example.weather_service.data.WeatherDataRepository;
import com.example.weather_service.data.entities.WeatherData;
import com.example.weather_service.service.WeatherDataMapper;
import com.example.weather_service.service.WeatherDataService;
import com.example.weather_service.service.dto.AverageDataDto;
import com.example.weather_service.service.dto.WeatherDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherDataServiceImpl implements WeatherDataService {

    private final WeatherDataMapper mapper;
    private final WeatherDataRepository repository;
    private final WeatherClient client;
    private final ObjectMapper objectMapper;
    private final String DECIMAL_FORMAT = "#0.00";

    @Override
    @Scheduled(fixedRateString = "${TIME_BETWEEN_REQUESTS}")
    public void save() {
        String response = client.getCurrent();
        log.info("RESPONSE : {}", response);
        try {
            WeatherDataDto dto = deserialize(response);
            repository.save(mapper.toEntity(dto));
            log.info("SAVED current {}", dto);
        } catch (IOException e) {
            log.error("EXCEPTION", e);
        }
    }

    @Override
    public WeatherDataDto getLatest() {
        Long lastInsertionId = repository.count();
        try {
            return mapper.toDto(repository.findById(lastInsertionId).orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No records found");
        }
    }

    @Override
    public AverageDataDto getAverageByPeriod(String request) throws JsonProcessingException, IllegalArgumentException {
        log.info(request);
        JsonNode node = objectMapper.readTree(request);
        String from = node.get("from").asText();
        Long fromEpoch = getEpochFrom(from);
        String to = node.get("to").asText();
        Long toEpoch = getEpochTo(to);
        log.info("FROM " + fromEpoch + " TO " + toEpoch);
        if (fromEpoch > toEpoch) {
            throw new IllegalArgumentException("End of the period must be later than start of the period");
        }
        List<WeatherData> included = repository.findAllEpochTimeBetween(getEpochFrom(from), getEpochTo(to));
        return getAverageDataDto(included);
    }

    private AverageDataDto getAverageDataDto(List<WeatherData> datas) {
        if (datas.isEmpty()) {
            throw new NoSuchElementException("ERROR : No elements included in the period of time");
        }
        AverageDataDto result = new AverageDataDto();
        result.setHumidity(getAverageHumidity(datas));
        result.setConditions(getAverageConditions(datas));
        result.setTemperature(getAverageTemperature(datas));
        result.setWindSpeed(getAverageWindSpeed(datas));
        result.setPressure(getAveragePressure(datas));
        return result;
    }

    private Double getAveragePressure(List<WeatherData> datas) {
        return getFormattedAverage(countAverage(datas.stream()
                .map(WeatherData::getPressure)
                .collect(Collectors.toList())));
    }

    private Double getAverageWindSpeed(List<WeatherData> datas) {
        return getFormattedAverage(countAverage(datas.stream()
                .map(WeatherData::getWindSpeed)
                .collect(Collectors.toList())));
    }

    private Double getAverageTemperature(List<WeatherData> datas) {
        return getFormattedAverage(countAverage(datas.stream()
                .map(WeatherData::getTemperature)
                .collect(Collectors.toList())));
    }

    private Double getFormattedAverage(Double average) {
        String formatted = new DecimalFormat(DECIMAL_FORMAT).format(average);
        return Double.parseDouble(formatted.replace(",", "."));
    }

    private int getAverageHumidity(List<WeatherData> datas) {
        List<Integer> humidityResults = datas.stream()
                .map(WeatherData::getHumidity)
                .toList();
        return countAverage(humidityResults.stream()
                .map(Double::valueOf)
                .collect(Collectors.toList()))
                .intValue();
    }

    private String getAverageConditions(List<WeatherData> datas) {
        Map<String, Integer> conditionsAndQuantity = new HashMap<>();
        for (WeatherData data : datas) {
            String conditions = data.getConditions();
            if (conditionsAndQuantity.containsKey(conditions)) {
                conditionsAndQuantity.replace(conditions, conditionsAndQuantity.get(conditions + 1));
            } else {
                conditionsAndQuantity.put(conditions, 1);
            }
        }
        return Collections.max(conditionsAndQuantity.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private Double countAverage(List<Double> values) {
        return values.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    private Long getEpochFrom(String dateFrom) {
        String[] split = dateFrom.split("-");
        LocalDate localDate = getReformattedLocalDate(split, split[0]);
        String epochSeconds = String.valueOf(localDate.atStartOfDay()
                        .toInstant(ZoneOffset.ofHours(3))
                        .toEpochMilli());
        return reformatToSeconds(epochSeconds);
    }

    private Long getEpochTo(String dateTo) {
        String[] split = dateTo.split("-");
        int theDayAfter = Integer.parseInt(split[0]) + 1;
        String dayAfter = String.valueOf(theDayAfter);
        String correctDayFormat = dayAfter.length() > 1 ? dayAfter : "0" + dayAfter;
        LocalDate localDate = getReformattedLocalDate(split, correctDayFormat);
        String epochSeconds = String.valueOf(localDate.atStartOfDay()
                        .toInstant(ZoneOffset.ofHours(3) )
                        .toEpochMilli() - 1);
        return reformatToSeconds(epochSeconds);
    }

    private static LocalDate getReformattedLocalDate(String[] split, String correctDayFormat) {
        String reformatted = String.format("%s-%s-%s", split[2], split[1], correctDayFormat);
        return LocalDate.parse(reformatted);
    }

    private WeatherDataDto deserialize(String response) throws IOException {
        JsonNode node = objectMapper.readTree(response);
        JsonNode currentWeatherNode = node.get("current");
        JsonNode locationNode = node.get("location");
        WeatherDataDto dto = new WeatherDataDto();
        dto.setEpochTime(Long.parseLong(String.valueOf(locationNode.get("localtime_epoch"))) + 10800);
        dto.setTemperature(currentWeatherNode.get("temp_c").asDouble());
        dto.setConditions(currentWeatherNode.get("condition").get("text").asText());
        dto.setPressure(currentWeatherNode.get("pressure_mb").asDouble());
        dto.setWindSpeed(currentWeatherNode.get("wind_kph").asDouble());
        dto.setHumidity(currentWeatherNode.get("humidity").asInt());
        dto.setLocation(node.get("location").get("name").asText());
        return dto;
    }

    private long reformatToSeconds(String epochSeconds) {
        return Long.parseLong(epochSeconds.substring(0, epochSeconds.length() - 3));
    }
}


