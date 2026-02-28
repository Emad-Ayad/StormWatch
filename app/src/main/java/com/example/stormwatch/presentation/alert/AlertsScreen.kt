package com.example.stormwatch.presentation.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import com.example.stormwatch.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.presentation.alert.util.AddAlertDialog
import com.example.stormwatch.presentation.alert.view_model.AlertViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import com.example.stormwatch.R

@Composable
fun AlertsScreen(
    navController: NavHostController,
    viewModel: AlertViewModel
) {
    val alerts by viewModel.alerts.collectAsState()
    val pendingLocation by viewModel.pendingLocation.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var alertToDelete by remember { mutableStateOf<AlertEntity?>(null) }

    if (pendingLocation != null) {
        val loc = pendingLocation!!
        AddAlertDialog(
            cityName = loc.city,
            onConfirm = { startTime, endTime, alertType ->
                viewModel.addAlert(loc.city, loc.lat, loc.lon, startTime, endTime, alertType)
                viewModel.clearPendingLocation()
            },
            onDismiss = {
                viewModel.clearPendingLocation()
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map_picker_alert") },
                containerColor = AccentYellow
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert), tint = Color.White)
            }
        }
    ) { paddingValues ->

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AccentYellow.copy(alpha = 0.3f)
                    )
                    Text(
                        stringResource(R.string.no_active_alerts),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        stringResource(R.string.tap_add_alert),
                        fontSize = 10.sp,
                        color = AccentYellow.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alerts, key = { it.id }) { alert ->
                    AlertItem(
                        alert = alert,
                        onDelete = {
                            alertToDelete = alert
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        if (showDeleteDialog && alertToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.stop_alert_title)) },
                text = {
                    Text(stringResource(R.string.stop_alert_confirm, alertToDelete!!.city))
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteAlert(alertToDelete!!)
                        showDeleteDialog = false
                    }) {
                        Text(stringResource(R.string.stop), color = Pink80)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.keep))
                    }
                }
            )
        }
    }
}

@Composable
fun AlertItem(
    alert: AlertEntity,
    onDelete: () -> Unit
) {
    val fmt = remember { SimpleDateFormat("MMM dd  hh:mm a", Locale.getDefault()) }

    val isAlarm = alert.alertType == "ALARM"
    val typeLabel = if (isAlarm) stringResource(R.string.alarm_sound) else stringResource(R.string.notification)
    val typeIcon = if (isAlarm) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.Notifications


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alert.city,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(AccentYellow.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(typeIcon,
                            contentDescription = null,
                            tint = AccentYellow,
                            modifier = Modifier.size(14.dp))

                        Text(typeLabel,
                            fontSize = 12.sp,
                            color = AccentYellow,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.from),
                        fontSize = 10.sp,
                        color = PurpleGrey40)
                    Text(fmt.format(Date(alert.startTime)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.until),
                        fontSize = 10.sp,
                        color = PurpleGrey40)

                    Text(fmt.format(Date(alert.endTime)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium)
                }
            }

            Text(
                "${stringResource(R.string.lat_format, alert.lat)}, ${stringResource(R.string.lon_format, alert.lon).replace("Lat", "Lon")}",
                fontSize = 12.sp,
                color = PurpleGrey40
            )

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Pink80
                ),
            ) {
                Icon(Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(16.dp))

                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.stop_alert))
            }
        }
    }
}