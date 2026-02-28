package com.example.stormwatch.presentation.alert.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stormwatch.data.datasource.alert_service.AlertLocalDataSource
import com.example.stormwatch.data.datasource.db.MapsDataBase
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.repo.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class AlertViewModel(private val repo: AlertRepository) : ViewModel() {

    val alerts: StateFlow<List<AlertEntity>> = repo.getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendingLocation = MutableStateFlow<PendingAlertLocation?>(null)
    val pendingLocation: StateFlow<PendingAlertLocation?> = _pendingLocation.asStateFlow()

    fun setPendingLocation(city: String, lat: Double, lon: Double) {
        _pendingLocation.value = PendingAlertLocation(city, lat, lon)
    }

    fun clearPendingLocation() {
        _pendingLocation.value = null
    }

    fun addAlert(
        city: String,
        lat: Double,
        lon: Double,
        start: Long,
        end: Long,
        type: String
    ) {
        viewModelScope.launch {
            repo.addAlert(lat, lon, city, start, end, type)
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repo.deleteAlert(alert)
        }
    }
}

data class PendingAlertLocation(
    val city: String,
    val lat: Double,
    val lon: Double
)

class AlertViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = MapsDataBase.getInstance(context)
        val dao = database.alertDao()
        val localDataSource = AlertLocalDataSource(dao)
        val repository = AlertRepository(localDataSource, context)
        return AlertViewModel(repository) as T
    }
}