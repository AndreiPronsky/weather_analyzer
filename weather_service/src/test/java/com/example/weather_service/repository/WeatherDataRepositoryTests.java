package com.example.weather_service.repository;

import com.example.weather_service.data.WeatherDataRepository;
import com.example.weather_service.data.entities.WeatherData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class WeatherDataRepositoryTests {

    @Autowired
    private WeatherDataRepository repository;

    @Test
    public void WeatherDataRepository_Save_ReturnSavedData() {
        WeatherData weatherData = WeatherData.builder()
                .epochTime(1704716060L)
                .temperature(22.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();

        WeatherData savedData = repository.save(weatherData);

        Assertions.assertThat(savedData).isNotNull();
        Assertions.assertThat(savedData.getId()).isGreaterThan(0);
    }

    @Test
    public void WeatherDataRepository_FindAllEpochBetween_ReturnMoreThanOne() {
        long timeFrom = 1704710000;
        long timeTo = 1704720000;
        WeatherData weatherData1 = WeatherData.builder()
                .epochTime(1704716050L)
                .temperature(21.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();
        WeatherData weatherData2 = WeatherData.builder()
                .epochTime(1704716060L)
                .temperature(22.0)
                .windSpeed(15.0)
                .pressure(1000.1)
                .humidity(55)
                .conditions("Snowing")
                .location("Minsk").build();

        repository.save(weatherData1);
        repository.save(weatherData2);

        List<WeatherData> weatherDataList = repository.findAllEpochTimeBetween(timeFrom, timeTo);

        Assertions.assertThat(weatherDataList).isNotEmpty();
        Assertions.assertThat(weatherDataList.size()).isEqualTo(2);
    }
}
