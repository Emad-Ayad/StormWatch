package com.example.stormwatch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val lat: Double,
    val lon: Double,
    val city: String,
    val startTime: Long,
    val endTime: Long,
    val alertType: String,
    val isActive: Boolean = true
)