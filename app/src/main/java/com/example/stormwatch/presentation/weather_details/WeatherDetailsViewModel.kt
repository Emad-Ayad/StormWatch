package com.example.stormwatch.presentation.weather_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.repo.WeatherRepository
import com.example.stormwatch.presentation.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WeatherDetailsViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun getWeatherFromFav(lat: Double, lon: Double) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val data = repo.getForecast(
                    lat = lat,
                    lon = lon,
                    units = "metric",
                    lang = "en"
                )
                _state.value = UiState.Success(data)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}

class WeatherDetailsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val remoteDataSource = RemoteDataSource()
        val repo = WeatherRepository(remoteDataSource)
        return WeatherDetailsViewModel(repo) as T
    }
}