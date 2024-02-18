package com.example.weather_service.data;

import com.example.weather_service.data.entities.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    @Query("FROM WeatherData wd WHERE wd.epochTime BETWEEN :start_period AND :end_period")
    List<WeatherData> findAllEpochTimeBetween(@Param("start_period") Long startPeriod, @Param("end_period") Long endPeriod);
}
