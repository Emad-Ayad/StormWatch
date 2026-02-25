package com.example.stormwatch.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.stormwatch.data.model.GeoCodingDto

interface GeoService {

    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") city: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeoCodingDto>
}