package com.example.stormwatch.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.stormwatch.data.datasource.remote.GeoService

object GeoRetrofit {

    private val geo = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org")
        .build()

    val geoService: GeoService = geo.create(GeoService::class.java)
}


