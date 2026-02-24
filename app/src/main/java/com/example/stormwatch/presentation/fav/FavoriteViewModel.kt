package com.example.stormwatch.presentation.fav

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stormwatch.data.repo.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.stormwatch.data.model.FavoriteEntity
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.stormwatch.data.datasource.db.MapsDataBase
import com.example.stormwatch.data.datasource.local.FavoriteLocalDataSource



class FavoriteViewModel( private val repo: FavoriteRepository) : ViewModel() {

    val favorites = repo.getFavorites()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addFavorite(city: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            repo.addFavorite(city, lat, lon)
        }
    }

    fun deleteFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repo.deleteFavorite(favorite)
        }
    }

}

class FavoritesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val database = MapsDataBase.getInstance(context)
        val dao = database.favDao()
        val localDataSource = FavoriteLocalDataSource(dao)
        val repository = FavoriteRepository(localDataSource)

        return FavoriteViewModel(repository) as T
    }
}