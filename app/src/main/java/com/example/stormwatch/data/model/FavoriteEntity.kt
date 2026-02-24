package com.example.stormwatch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val cityName: String,
    val lat: Double,
    val lon: Double
)