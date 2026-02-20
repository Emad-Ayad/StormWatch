package com.example.stormwatch.presentation.settings

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import com.example.stormwatch.data.settings.SettingsStore
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SettingsViewModel(private val settings: SettingsStore) : ViewModel() {

    val settingsState: StateFlow<Pair<String, String>> =
        settings.settingsFlow.stateIn(viewModelScope, SharingStarted.Lazily, "metric" to "en")

    val locationMethod: StateFlow<String> =
        settings.locationMethodFlow.stateIn(viewModelScope, SharingStarted.Lazily, "gps")

    val location: StateFlow<Pair<Double?, Double?>> =
        settings.locationFlow.stateIn(viewModelScope, SharingStarted.Lazily, null to null)

    fun updateUnits(units: String) {
        viewModelScope.launch {
            settings.saveUnits(units)
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            settings.saveLang(lang)
        }
    }

    fun updateLocationMethod(method: String) {
        viewModelScope.launch { settings.saveLocationMethod(method) }
    }

    fun fetchGpsLocation(context: Context) {
        viewModelScope.launch {
            try {
                val loc = context.getCurrentLocationSuspend()
                settings.saveLocation(loc.latitude, loc.longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun Context.getCurrentLocationSuspend(): Location =
        suspendCancellableCoroutine { cont ->
            val fusedClient = LocationServices.getFusedLocationProviderClient(this)
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(loc)
                    else cont.resumeWithException(Exception("Failed to get location"))
            }.addOnFailureListener { cont.resumeWithException(it) }
        }

}



class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val settings = SettingsStore(context)
        return SettingsViewModel(settings) as T
    }
}