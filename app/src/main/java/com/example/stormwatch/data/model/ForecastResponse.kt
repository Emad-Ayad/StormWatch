package com.example.stormwatch.data.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val city: City,
    val list: List<ForecastItem>
)

data class City(
    val name: String,
    val coord: Coord
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class ForecastItem(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDto>,
    val clouds: CloudsDto,
    val wind: WindDto,
    @SerializedName("dt_txt") val dateText: String
)

data class MainDto(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class WeatherDto(
    val description: String,
    val icon: String
)

data class DayForecast(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String
)

data class CloudsDto(
    @SerializedName("all")
    val all: Int
)

data class WindDto(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)