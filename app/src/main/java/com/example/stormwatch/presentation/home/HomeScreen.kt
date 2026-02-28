package com.example.stormwatch.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.stormwatch.presentation.utils.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.stormwatch.R
import com.example.stormwatch.ui.theme.*
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.DayForecast
import com.example.stormwatch.presentation.home.view_model.HomeViewModel
import com.example.stormwatch.presentation.home.view_model.WeatherStates
import com.example.stormwatch.presentation.home.view_model.HomeViewModelFactory
import kotlin.math.roundToInt
import com.example.stormwatch.presentation.settings.SettingsViewModel
import com.example.stormwatch.presentation.settings.SettingsViewModelFactory
import androidx.compose.ui.res.stringResource


@Composable
fun HomeScreen(navController: NavHostController){

    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(settingsViewModel))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            settingsViewModel.ensureGpsLocation(context)
        }
    }

    LaunchedEffect(Unit) {

        val hasPermission = ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            settingsViewModel.ensureGpsLocation(context)
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Image(
            painter = painterResource(id = R.drawable.weather_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        when (uiState) {
            is WeatherStates.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            is WeatherStates.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = (uiState as WeatherStates.Error).message ?: "Unknown error", color = Color.Red)
                }

            }
            is WeatherStates.Success -> {
                val forecast = (uiState as WeatherStates.Success).forecast
                if (forecast.list.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.location_not_set),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(top = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(text = stringResource(R.string.welcome_message), color = SubtleOnGlass,)

                        CurrentWeatherCard(forecast)
                        MetricsRow(forecast)
                        HourlyStrip(forecast)
                        ForecastTabs(forecast)

                        Spacer(Modifier.height(18.dp))
                    }
                }
            }
        }
    }
}



@Composable
fun CurrentWeatherCard(forecast : ForecastResponse) {
    val current = forecast.list.firstOrNull()

    val iconUrl = "https://openweathermap.org/img/wn/${current?.weather?.firstOrNull()?.icon}@2x.png"

    MyCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(Modifier.weight(1f)) {
                Text(forecast.city.name, color = OnGlass, fontWeight = FontWeight.SemiBold)


                Text(
                    current?.dateText ?: stringResource(R.string.unknown),
                    color = SubtleOnGlass,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium)

                Spacer(Modifier.height(12.dp))

                Text(current?.main?.temp?.roundToInt().toString(), color = OnGlass,fontSize = 38.sp,
                    fontWeight = FontWeight.Medium) // TODO put the temp

                current?.weather?.first()?.description?.let { Text(it, color = AccentYellow, fontWeight = FontWeight.Medium) }
            }


            Box(
                modifier = Modifier.height(120.dp).width(170.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun MetricsRow(forecast : ForecastResponse) {
    val current = forecast.list.firstOrNull()
    MyCard(containerColor = GlassDarkStrong) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricItem("${current?.wind?.speed} " + stringResource(R.string.km_h), stringResource(R.string.metric_wind), R.drawable.wind) // TODO change this km/h
            MetricItem("${current?.main?.humidity} %", stringResource(R.string.metric_humidity), R.drawable.humidity)
            MetricItem("${current?.clouds?.all} %", stringResource(R.string.metric_clouds), R.drawable.rain)
            MetricItem("${current?.main?.pressure} " + stringResource(R.string.hpa), stringResource(R.string.metric_pressure), R.drawable.pressure)
        }
    }
}



@Composable
fun ForecastTabs(forecast: ForecastResponse) {
    MyCard(containerColor = GlassDarkStrong, padding = PaddingValues(0.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            forecast.nextFiveDays().forEach { day ->
                DayTab(day)
            }
        }
    }
}


private fun ForecastResponse.nextFiveDays(): List<DayForecast> {
    val today = this.list.firstOrNull()?.dateText?.take(10) ?: ""
    return this.list
        .filter { it.dateText.take(10) != today }
        .groupBy { it.dateText.take(10) }
        .toSortedMap()
        .entries
        .take(5)
        .map { (date, items) ->
            val temps = items.map { it.main.temp }
            val icon = items[items.size / 2].weather.firstOrNull()?.icon ?: "01d"
            DayForecast(date, temps.minOrNull() ?: 0.0, temps.maxOrNull() ?: 0.0, icon)
        }
}

@Composable
fun HourlyStrip(forecast : ForecastResponse) {
    val todayDate = forecast.list.firstOrNull()?.dateText?.take(10) ?: ""
    val todayHours = forecast.list.filter { it.dateText.take(10) == todayDate }
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        todayHours.take(12).forEach { item ->
            HourChip(
                hour = item.dateText.substringAfterLast(" ").take(5),
                desc = item.main.temp.roundToInt().toString(),
                icon = item.weather.firstOrNull()?.icon ?: "01d"
            )
        }
    }
}
