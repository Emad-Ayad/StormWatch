package com.example.stormwatch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route: String,
                     val label: String,
                     val icon: ImageVector
) {
    object HomeScreen : Screens(
        route = "HomeScreen",
        label = "Home",
        icon = Icons.Default.Home
    )

    object SettingsScreen : Screens(
        route = "SettingsScreen",
        label = "Settings",
        icon = Icons.Default.Settings
    )
}