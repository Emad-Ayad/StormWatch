package com.example.stormwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stormwatch.presentation.home.HomeScreen
import com.example.stormwatch.ui.theme.StormWatchTheme
import com.example.stormwatch.presentation.settings.SettingsScreen
import com.example.stormwatch.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StormWatchTheme {
                Scaffold( modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                    MyApp(    )
                }
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
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screens.HomeScreen,
        Screens.SettingsScreen
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