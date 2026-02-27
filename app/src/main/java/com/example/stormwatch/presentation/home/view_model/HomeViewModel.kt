package com.example.stormwatch.presentation.home.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stormwatch.data.repo.WeatherRepository
import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.CurrentWeatherResponse
import com.example.stormwatch.data.model.City
import com.example.stormwatch.data.model.Coord
import kotlinx.coroutines.launch
import com.example.stormwatch.presentation.settings.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class HomeViewModel(private val repo : WeatherRepository,
                    private val settingsViewModel: SettingsViewModel) : ViewModel() {
    private val _uiState: MutableStateFlow<WeatherStates> = MutableStateFlow<WeatherStates>(WeatherStates.Loading)
    val uiState: StateFlow<WeatherStates> = _uiState.asStateFlow()

    init {
        getForecast()
    }

    fun getForecast() {
        viewModelScope.launch {
            combine(
                settingsViewModel.settingsState,
                settingsViewModel.location
            ) { settings, location ->
                Triple(settings.first, settings.second, location)
            }.collectLatest { (units, lang, location) ->

                val lat = location.first
                val lon = location.second

                if (lat == null || lon == null) {
                    _uiState.value = WeatherStates.Error("Location not available")
                    return@collectLatest
                }

                _uiState.value = WeatherStates.Loading

                try {
                    val forecast = repo.getForecast(
                        lat = lat,
                        lon = lon,
                        units = units,
                        lang = lang
                    )
                    _uiState.value = WeatherStates.Success(forecast)
                }catch (e: Exception) {
                    _uiState.value = WeatherStates.Error(e.message ?: "Error try again Later")
                }
            }
        }
    }

}

sealed class WeatherStates{
    object Loading : WeatherStates()
    data class Success(val forecast: ForecastResponse) : WeatherStates()
    data class Error(val message: String) : WeatherStates()
}

class HomeViewModelFactory(private val settingsViewModel: SettingsViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val remoteDataSource = RemoteDataSource()
        val repo = WeatherRepository(remoteDataSource)
        return HomeViewModel(repo,settingsViewModel) as T
    }
}