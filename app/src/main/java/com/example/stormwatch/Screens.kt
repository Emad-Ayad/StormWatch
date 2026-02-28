package com.example.stormwatch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
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

    object FavScreen : Screens(
        route = "FavScreen",
        label = "Favorite",
        icon = Icons.Default.Favorite
    )

    object AlertScreen : Screens(
        route = "AlertScreen",
        label = "Alert",
        icon = Icons.Default.Notifications
    )
}