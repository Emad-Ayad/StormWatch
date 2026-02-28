package com.example.stormwatch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.stormwatch.R

sealed class Screens(val route: String,
                     val label: Int,
                     val icon: ImageVector
) {
    object HomeScreen : Screens(
        route = "HomeScreen",
        label = R.string.nav_home,
        icon = Icons.Default.Home
    )

    object SettingsScreen : Screens(
        route = "SettingsScreen",
        label = R.string.nav_settings,
        icon = Icons.Default.Settings
    )

    object FavScreen : Screens(
        route = "FavScreen",
        label = R.string.nav_favorite,
        icon = Icons.Default.Favorite
    )

    object AlertScreen : Screens(
        route = "AlertScreen",
        label = R.string.nav_alert,
        icon = Icons.Default.Notifications
    )
}