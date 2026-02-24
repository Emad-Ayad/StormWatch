package com.example.stormwatch.data.repo

import kotlinx.coroutines.flow.Flow
import com.example.stormwatch.data.datasource.local.FavoriteLocalDataSource
import com.example.stormwatch.data.model.FavoriteEntity


class FavoriteRepository(private val localDataSource: FavoriteLocalDataSource) {

    fun getFavorites(): Flow<List<FavoriteEntity>> {
        return localDataSource.getFavorites()
    }

    suspend fun addFavorite(
        cityName: String,
        lat: Double,
        lon: Double
    ) {
        localDataSource.insertFavorite(
            FavoriteEntity(
                cityName = cityName,
                lat = lat,
                lon = lon
            )
        )
    }

    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        localDataSource.deleteFavorite(favorite)
    }

}