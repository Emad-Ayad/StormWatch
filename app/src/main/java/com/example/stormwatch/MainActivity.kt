package com.example.stormwatch

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import com.example.stormwatch.presentation.map.MapScreen
import com.example.stormwatch.presentation.settings.SettingsViewModel
import com.example.stormwatch.presentation.settings.SettingsViewModelFactory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stormwatch.presentation.home.HomeScreen
import com.example.stormwatch.ui.theme.StormWatchTheme
import com.example.stormwatch.presentation.settings.SettingsScreen
import com.example.stormwatch.presentation.fav.FavoritesScreen
import com.example.stormwatch.presentation.fav.FavoriteViewModel
import com.example.stormwatch.presentation.fav.FavoritesViewModelFactory
import com.example.stormwatch.presentation.weather_details.WeatherDetailsScreen
import com.example.stormwatch.ui.theme.*
import com.example.stormwatch.presentation.alert.*
import com.example.stormwatch.presentation.alert.view_model.AlertViewModel
import com.example.stormwatch.presentation.alert.view_model.AlertViewModelFactory
import kotlinx.coroutines.runBlocking
import com.example.stormwatch.data.settings.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking {
            newBase.dataStore.data
                .map { it[stringPreferencesKey("lang")] ?: "en" }
                .first()
        }
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, lang))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
        setContent {
            StormWatchTheme {
                    MyApp()
            }
        }
    }
}
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lang by context.dataStore.data
        .map { it[stringPreferencesKey("lang")] ?: "en" }
        .collectAsState(initial = "en")

    val layoutDirection =
        if (lang == "ar") LayoutDirection.Rtl
        else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        val navController = rememberNavController()
        Scaffold(
            modifier = modifier,
            bottomBar = {
                BottomNavBar(navController)
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "HomeScreen",
                modifier = Modifier.padding(padding)
            ) {
                composable("HomeScreen") {
                    HomeScreen(navController)
                }
                composable("SettingsScreen") {
                    SettingsScreen(navController)
                }
                composable("map_picker_settings") {
                    val context = LocalContext.current
                    val settingsViewModel: SettingsViewModel =
                        viewModel(factory = SettingsViewModelFactory(context))

                    MapScreen(navController, onLocationSelected = { city ->
                        settingsViewModel.saveMapLocation(city.lat, city.lon)
                        navController.popBackStack()
                    }
                    )
                }
                composable("map_picker_favorite") {
                    val context = LocalContext.current
                    val favoritesViewModel: FavoriteViewModel =
                        viewModel(factory = FavoritesViewModelFactory(context))

                    MapScreen(navController, onLocationSelected = { city ->
                        favoritesViewModel.addFavorite(
                            city = city.name,
                            lat = city.lat,
                            lon = city.lon
                        )
                    }
                    )
                }
                composable("map_picker_alert") {
                    val context = LocalContext.current

                    val graphEntry = remember(navController.currentBackStackEntry) {
                        navController.getBackStackEntry(navController.graph.id)
                    }
                    val alertsViewModel: AlertViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = AlertViewModelFactory(context)
                    )

                    MapScreen(navController, onLocationSelected = { city ->
                        alertsViewModel.setPendingLocation(city.name, city.lat, city.lon)
                    })
                }
                composable(
                    route = "weather_details/{lat}/{lon}/{city}",
                    arguments = listOf(
                        navArgument("lat") { type = NavType.StringType },
                        navArgument("lon") { type = NavType.StringType },
                        navArgument("city") { type = NavType.StringType }
                    )
                ) {

                    val lat = it.arguments?.getString("lat")?.toDouble() ?: 0.0
                    val lon = it.arguments?.getString("lon")?.toDouble() ?: 0.0
                    val city = it.arguments?.getString("city") ?: "UnKnown"

                    WeatherDetailsScreen(
                        lat = lat,
                        lon = lon,
                        cityName = city,
                        navController = navController
                    )
                }

                composable("FavScreen") {
                    FavoritesScreen(navController)
                }

                composable("AlertScreen") {
                    val context = LocalContext.current
                    val graphEntry = remember(navController.currentBackStackEntry) {
                        navController.getBackStackEntry(navController.graph.id)
                    }
                    val alertsViewModel: AlertViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = AlertViewModelFactory(context)
                    )
                    AlertsScreen(navController = navController, viewModel = alertsViewModel)
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screens.HomeScreen,
        Screens.SettingsScreen,
        Screens.FavScreen,
        Screens.AlertScreen
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.Black) {
        items.forEachIndexed { _, screen ->

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label.toString(),
                        tint = OnGlass
                    )
                },
                label = { Text(text = stringResource(screen.label), color = OnGlass) }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StormWatchTheme {
        MyApp()
    }
}