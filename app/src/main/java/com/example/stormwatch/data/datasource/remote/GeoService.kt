package com.example.stormwatch.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.stormwatch.data.model.GeoCodingDto
import com.example.stormwatch.data.model.ReverseGeoDto

interface GeoService {

    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") city: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeoCodingDto>

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<ReverseGeoDto>
}