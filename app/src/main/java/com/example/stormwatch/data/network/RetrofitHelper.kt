package com.example.stormwatch.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.stormwatch.data.datasource.remote.WeatherService

object RetrofitHelper {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build()

    val weatherService: WeatherService = retrofit.create(WeatherService::class.java)
}

