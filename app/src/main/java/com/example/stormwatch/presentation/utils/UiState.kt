package com.example.stormwatch.presentation.utils

import com.example.stormwatch.data.model.ForecastResponse

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: ForecastResponse) : UiState()
    data class Error(val msg: String) : UiState()
}