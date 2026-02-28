package com.example.stormwatch.presentation.fav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.model.FavoriteEntity
import com.example.stormwatch.ui.theme.AccentYellow
import com.example.stormwatch.ui.theme.Pink80
import com.example.stormwatch.ui.theme.PurpleGrey40
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun FavoritesScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: FavoriteViewModel =
        viewModel(factory = FavoritesViewModelFactory(context))
    val favorites by viewModel.favorites.collectAsState()
    var showDeleteDialog = remember { mutableStateOf(false) }
    var favoriteToDelete by remember { mutableStateOf<FavoriteEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("map_picker_favorite")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Favorite")
            }
        }
    ) { padding ->

        if (favorites.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "No favorite locations yet",
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Add your first location",
                    color = Color.Gray
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                this.items(
                    items = favorites,
                    key = { it.id }
                ) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onDelete = {
                            favoriteToDelete = favorite
                            showDeleteDialog.value = true
                        },
                        onClick = {
                            navController.navigate(
                                "weather_details/${favorite.lat}/${favorite.lon}/${favorite.cityName}"
                            )
                        }
                    )
                    if (showDeleteDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteDialog.value = false
                            },
                            title = { Text("Delete Location") },
                            text = { Text("Are you sure you want to delete this Location?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteDialog.value = false
                                        viewModel.deleteFavorite(favoriteToDelete!!)
                                    }
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteDialog.value = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = favorite.cityName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lat: ${"%.4f".format(favorite.lat)}",
                    fontSize = 12.sp,
                )

                Text(
                    text = "Lat: ${"%.4f".format(favorite.lon)}",
                    fontSize = 12.sp,
                )
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Pink80
                ),
            ) {
                Icon(Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(16.dp))

                Spacer(Modifier.width(6.dp))
                Text("Delete Favorite")
            }
        }
    }
}