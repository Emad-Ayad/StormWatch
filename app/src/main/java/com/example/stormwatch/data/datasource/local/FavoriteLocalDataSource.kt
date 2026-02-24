package com.example.stormwatch.data.datasource.local

import kotlinx.coroutines.flow.Flow
import com.example.stormwatch.data.model.FavoriteEntity



class FavoriteLocalDataSource(private val dao: FavoritesDao) {


    fun getFavorites(): Flow<List<FavoriteEntity>> {
        return dao.getAllFavorites()
    }

    suspend fun insertFavorite(favorite: FavoriteEntity) {
        dao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        dao.deleteFavorite(favorite)
    }

}