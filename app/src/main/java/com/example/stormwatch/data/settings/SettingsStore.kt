package com.example.stormwatch.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")


class SettingsStore(private val context: Context) {


    companion object {
        val UNITS = stringPreferencesKey("units")
        val LANG = stringPreferencesKey("lang")
    }

    val settingsFlow: Flow<Pair<String, String>> =
        context.dataStore.data.map { prefs ->
            val units = prefs[UNITS] ?: "metric"
            val lang = prefs[LANG] ?: "en"
            units to lang
        }

    suspend fun saveUnits(units: String) {
        context.dataStore.edit { it[UNITS] = units }
    }

    suspend fun saveLang(lang: String) {
        context.dataStore.edit { it[LANG] = lang }
    }


}

