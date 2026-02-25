package com.example.stormwatch.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.stormwatch.data.model.GeoCodingDto
import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.repo.WeatherRepository

class MapViewModel(private val repo : WeatherRepository) : ViewModel() {

    private val _results = MutableStateFlow<List<GeoCodingDto>>(emptyList())
    val results: StateFlow<List<GeoCodingDto>> = _results

    fun searchCity(query: String) {
        if (query.length < 3) {
            _results.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _results.value = repo.searchCity(city = query)
            } catch (e: Exception) {
                _results.value = emptyList()
            }
        }
    }

    fun reverseGeocodeAndSelect(
        lat: Double,
        lon: Double,
        onSelected: (GeoCodingDto) -> Unit
    ) {
        viewModelScope.launch {
            val place = repo.reverseGeocode(lat, lon)

            val city = GeoCodingDto(
                name = place?.name ?: "Unknown location",
                country = place?.country ?: "",
                lat = lat,
                lon = lon
            )

            onSelected(city)
        }
    }

}

class MapViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val remoteDataSource = RemoteDataSource()
        val repo = WeatherRepository(remoteDataSource)
        return MapViewModel(repo) as T
    }
}