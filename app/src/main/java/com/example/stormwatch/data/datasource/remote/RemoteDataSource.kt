package com.example.stormwatch.data.datasource.remote

import com.example.stormwatch.data.network.RetrofitHelper
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.CurrentWeatherResponse
import com.example.stormwatch.BuildConfig
import com.example.stormwatch.data.model.GeoCodingDto
import com.example.stormwatch.data.model.ReverseGeoDto
import com.example.stormwatch.data.network.GeoRetrofit


class RemoteDataSource {
    private val weatherService :WeatherService
    private val geoService : GeoService

    init {
        weatherService = RetrofitHelper.weatherService
        geoService = GeoRetrofit.geoService
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

    suspend fun searchCity(city: String) : List<GeoCodingDto> {
        return geoService.searchCity(
            city = city,
            apiKey = BuildConfig.WEATHER_API_KEY
        )
    }


    suspend fun reverseGeocode(
        lat: Double,
        lon: Double
    ): ReverseGeoDto? {
        return geoService.reverseGeocode(
            lat=lat,
            lon=lon,
            apiKey = BuildConfig.WEATHER_API_KEY
        ).firstOrNull()
    }




}