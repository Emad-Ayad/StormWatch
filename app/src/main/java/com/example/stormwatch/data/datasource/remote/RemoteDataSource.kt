package com.example.stormwatch.data.datasource.remote

import com.example.stormwatch.data.network.RetrofitHelper
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.CurrentWeatherResponse
import com.example.stormwatch.BuildConfig


class RemoteDataSource {
    private val weatherService :WeatherService

    init {
        weatherService = RetrofitHelper.weatherService
    }


    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): ForecastResponse {
        return weatherService.getForecast(
            lat = lat,
            lon = lon,
            apiKey =  BuildConfig.WEATHER_API_KEY,
            units = units,
            lang = lang
        )
    }

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): CurrentWeatherResponse  {
        return weatherService.getCurrentWeather(
            lat = lat,
            lon = lon,
            apiKey =  BuildConfig.WEATHER_API_KEY,
            units = units,
            lang = lang
        )
    }

    suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String?,
        lang: String
    ): ForecastResponse {
        return weatherService.getDailyForecast(
            lat = lat,
            lon = lon,
            apiKey =  BuildConfig.WEATHER_API_KEY,
            units = units,
            lang = lang
        )
    }



}