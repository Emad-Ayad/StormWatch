package com.example.stormwatch.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import com.example.stormwatch.data.settings.SettingsStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settings: SettingsStore) : ViewModel() {

    val settingsState: StateFlow<Pair<String, String>> =
        settings.settingsFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, "metric" to "en")

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
}



class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val settings = SettingsStore(context)
        return SettingsViewModel(settings) as T
    }
}