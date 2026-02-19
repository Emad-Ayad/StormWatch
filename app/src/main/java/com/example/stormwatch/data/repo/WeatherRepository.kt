package com.example.stormwatch.data.repo

import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.model.CurrentWeatherResponse
import com.example.stormwatch.data.model.ForecastResponse


class WeatherRepository(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): ForecastResponse{
       return remoteDataSource.getForecast(lat, lon, units, lang)
    }
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): CurrentWeatherResponse{
        return remoteDataSource.getCurrentWeather(lat, lon, units, lang)
    }

    suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): ForecastResponse{
        return remoteDataSource.getDailyForecast(lat, lon, units, lang)
    }
}