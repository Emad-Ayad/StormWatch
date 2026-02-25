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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class HomeViewModel(private val repo : WeatherRepository,
                    private val settingsViewModel: SettingsViewModel) : ViewModel() {
    private val _forecast: MutableState<ForecastResponse> = mutableStateOf(ForecastResponse(
        city = City("", Coord(0.0, 0.0)),
        list = emptyList()))
    val forecast: State<ForecastResponse>
        get() = _forecast

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> //TODO use flow
        get() = _error

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> //TODO use flow
        get() = _isLoading

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

                if (lat == null || lon == null) return@collectLatest

                _isLoading.postValue(true)
                try {
                    _forecast.value = repo.getForecast(
                        lat = lat,
                        lon = lon,
                        units = units,
                        lang = lang
                    )
                } catch (e: Exception) {
                    _error.postValue(e.message)
                } finally {
                    _isLoading.postValue(false)
                }
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