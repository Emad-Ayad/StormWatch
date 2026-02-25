package com.example.stormwatch.presentation.weather_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stormwatch.presentation.utils.UiState
import com.example.stormwatch.presentation.home.*


@Composable
fun WeatherDetailsScreen(
    lat: Double,
    lon: Double,
    cityName: String,
    navController: NavHostController
) {
    val viewModel: WeatherDetailsViewModel =
        viewModel(factory = WeatherDetailsViewModelFactory())

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeatherFromFav(lat, lon)
    }

    when (state) {
        is UiState.Loading -> {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val forecast = (state as UiState.Success).data

            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                CurrentWeatherCard(forecast)
                MetricsRow(forecast)
                HourlyStrip(forecast)
                ForecastTabs(forecast)
            }
        }

        is UiState.Error -> {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Failed to load weather")
            }
        }
    }
}