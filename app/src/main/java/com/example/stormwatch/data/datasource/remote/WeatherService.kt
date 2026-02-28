package com.example.stormwatch.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.CurrentWeatherResponse


interface WeatherService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String?,
        @Query("lang") lang: String
    ): ForecastResponse
}