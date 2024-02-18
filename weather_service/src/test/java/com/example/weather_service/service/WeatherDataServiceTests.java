package com.example.weather_service.service;

import com.example.weather_service.data.WeatherDataRepository;
import com.example.weather_service.data.entities.WeatherData;
import com.example.weather_service.service.dto.AverageDataDto;
import com.example.weather_service.service.dto.WeatherDataDto;
import com.example.weather_service.service.impl.WeatherDataServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherDataServiceTests {

    @Mock
    private WeatherDataRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WeatherDataMapper mapper;

    @InjectMocks
    private WeatherDataServiceImpl service;

    @Test
    public void WeatherDataService_GetLatest_ReturnsWeatherDataDto() {
        long id = 5;
        WeatherData data = WeatherData.builder()
                .id(id)
                .epochTime(1700000000L)
                .temperature(22.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();

        WeatherDataDto dataDto = WeatherDataDto.builder()
                .id(id)
                .epochTime(1700000000L)
                .temperature(22.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();

        when(repository.count()).thenReturn(id);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(data));
        when(mapper.toDto(data)).thenReturn(dataDto);

        WeatherDataDto returnedData = service.getLatest();

        Assertions.assertThat(returnedData).isNotNull();
    }

    @Test
    public void WeatherDataService_GetLatest_ThrowsNoSuchElementException() {
        when(repository.count()).thenReturn(0L);
        when(repository.findById(any())).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> service.getLatest());

        assertThat(thrown)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No records");
    }

    @Test
    public void WeatherDataService_GetAverage_ReturnsAverageDataDto() throws IOException {
        WeatherData weatherData1 = WeatherData.builder()
                .epochTime(1700000000L)
                .temperature(22.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();

        WeatherData weatherData2 = WeatherData.builder()
                .epochTime(1710000000L)
                .temperature(0.0)
                .windSpeed(0.0)
                .pressure(1000.1)
                .humidity(45)
                .conditions("Snowing")
                .location("Minsk").build();

        List<WeatherData> datas = new ArrayList<>();
        datas.add(weatherData1);
        datas.add(weatherData2);

        String jsonString = """
                {         "from": "14-09-2023",
                         "to" : "09-03-2024"
                }""";

        ObjectMapper innerMapper = new ObjectMapper();
        JsonNode node = innerMapper.readTree(jsonString);

        when(repository.findAllEpochTimeBetween(any(), any())).thenReturn(datas);
        when(objectMapper.readTree(any(String.class))).thenReturn(node);

        AverageDataDto averageDataDto = service.getAverageByPeriod(jsonString);

        Assertions.assertThat(averageDataDto).isNotNull();
        Assertions.assertThat(averageDataDto.getTemperature()).isEqualTo(11.0);
        Assertions.assertThat(averageDataDto.getHumidity()).isEqualTo(50);
        Assertions.assertThat(averageDataDto.getPressure()).isEqualTo(1000.1);
        Assertions.assertThat(averageDataDto.getWindSpeed()).isEqualTo(7.5);
        Assertions.assertThat(averageDataDto.getConditions()).isEqualTo("Snowing");

    }

    @Test
    public void WeatherDataService_GetAverage_ThrowsNoSuchElementException() throws JsonProcessingException {
        String jsonString = """
                {         "from": "14-09-2023",
                         "to" : "09-03-2024"
                }""";

        ObjectMapper innerMapper = new ObjectMapper();
        JsonNode node = innerMapper.readTree(jsonString);

        when(repository.findAllEpochTimeBetween(any(), any())).thenReturn(new ArrayList<>());
        when(objectMapper.readTree(any(String.class))).thenReturn(node);

        Throwable thrown = catchThrowable(() -> service.getAverageByPeriod(jsonString));

        assertThat(thrown)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("ERROR");
    }

    @Test
    public void WeatherDataService_GetAverage_ThrowsIllegalArgumentException() throws JsonProcessingException {

        String jsonString = """
                {         "from": "09-03-2024",
                         "to" : "14-09-2023"
                }""";

        ObjectMapper innerMapper = new ObjectMapper();
        JsonNode node = innerMapper.readTree(jsonString);

        when(objectMapper.readTree(any(String.class))).thenReturn(node);

        Throwable thrown = catchThrowable(() -> service.getAverageByPeriod(jsonString));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End of the period");
    }
}
