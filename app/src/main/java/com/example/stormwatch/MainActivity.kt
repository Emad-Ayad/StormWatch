package com.example.stormwatch

import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stormwatch.presentation.home.HomeScreen
import com.example.stormwatch.ui.theme.StormWatchTheme
import com.example.stormwatch.presentation.settings.SettingsScreen
import com.example.stormwatch.presentation.fav.FavoritesScreen
import com.example.stormwatch.presentation.fav.FavoriteViewModel
import com.example.stormwatch.presentation.fav.FavoritesViewModelFactory
import com.example.stormwatch.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StormWatchTheme {
                    MyApp()
            }
        }
    }
}
@Composable
fun MyApp(modifier: Modifier = Modifier) {
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

                MapScreen(navController,onLocationSelected = { city ->
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
            composable("FavScreen") {
                FavoritesScreen(navController)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screens.HomeScreen,
        Screens.SettingsScreen,
        Screens.FavScreen
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
                        contentDescription = screen.label,
                        tint = OnGlass
                    )
                },
                label = { Text(text = screen.label, color = OnGlass) }
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