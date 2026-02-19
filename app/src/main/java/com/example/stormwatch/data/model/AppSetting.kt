package com.example.stormwatch.data.model

data class AppSettings(
    val language: String = "en",
    val tempUnit: String = "metric",
    val windUnit: String = "mps",
    val locationSource: String = "gps"
)
