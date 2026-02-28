package com.example.stormwatch.data.repo

import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.model.CurrentWeatherResponse
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.GeoCodingDto
import com.example.stormwatch.data.model.ReverseGeoDto


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

    suspend fun searchCity(city: String) : List<GeoCodingDto> {
        return remoteDataSource.searchCity(city)
    }

    suspend fun reverseGeocode(
        lat: Double,
        lon: Double
    ): ReverseGeoDto? {
        return remoteDataSource.reverseGeocode(lat, lon)
    }

}