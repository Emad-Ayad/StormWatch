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

class HomeViewModel(private val repo : WeatherRepository,
                    private val settingsViewModel: SettingsViewModel) : ViewModel() {
    private val _forecast: MutableState<ForecastResponse> = mutableStateOf(ForecastResponse(
        city = City("", Coord(0.0, 0.0)),
        list = emptyList()))
    val forecast: State<ForecastResponse>
        get() = _forecast

    private val _current: MutableState<CurrentWeatherResponse?> = mutableStateOf(null)
    val current: State<CurrentWeatherResponse?>
        get() = _current

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String>
        get() = _error

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getForecast(
        lat: Double,
        lon: Double,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (units, lang) = settingsViewModel.settingsState.value
                _forecast.value = repo.getForecast(lat, lon, units, lang)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getCurrentWeather(lat, lon, units, lang)
                _current.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

}

class HomeViewModelFactory(private val settingsViewModel: SettingsViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val remoteDataSource = RemoteDataSource()
        val repo = WeatherRepository(remoteDataSource)
        return HomeViewModel(repo,settingsViewModel) as T
    }
}