package com.example.stormwatch.presentation.alert.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Notifications
import com.example.stormwatch.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.*
import com.example.stormwatch.R


@Composable
fun AddAlertDialog(
    cityName: String,
    onConfirm: (startTime: Long, endTime: Long, alertType: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val defaultStart = System.currentTimeMillis() + 60_000L
    val defaultEnd = System.currentTimeMillis() + 3_600_000L

    var startTime by remember { mutableLongStateOf(defaultStart) }
    var endTime by remember { mutableLongStateOf(defaultEnd) }
    var selectedType by remember { mutableStateOf("NOTIFICATION") }

    var startError by remember { mutableStateOf(false) }
    var endError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                Text(
                    text = stringResource(R.string.add_weather_alert),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = cityName,
                    fontSize = 14.sp,
                    color = AccentYellow
                )

                TimePickerRow(
                    label = stringResource(R.string.start_time),
                    timeMillis = startTime,
                    isError = startError,
                    errorMessage = stringResource(R.string.validation_start_past),
                    context = context,
                    onTimePicked = {
                        startTime = it
                        startError = false
                    }
                )

                TimePickerRow(
                    label = stringResource(R.string.end_time),
                    timeMillis = endTime,
                    isError = endError,
                    errorMessage = stringResource(R.string.validation_end_before_start),
                    context = context,
                    onTimePicked = {
                        endTime = it
                        endError = false
                    }
                )


                Text(
                    text = stringResource(R.string.alert_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlertTypeCard(
                        label = stringResource(R.string.notification),
                        icon = Icons.Default.Notifications,
                        isSelected = selectedType == "NOTIFICATION",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "NOTIFICATION" }
                    )
                    AlertTypeCard(
                        label = stringResource(R.string.alarm_sound),
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        isSelected = selectedType == "ALARM",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "ALARM" }
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            val now = System.currentTimeMillis()
                            startError = startTime <= now
                            endError = endTime <= startTime

                            if (!startError && !endError) {
                                onConfirm(startTime, endTime, selectedType)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.save_alert))
                    }
                }
            }
        }
    }
}

@Composable
private fun TimePickerRow(
    label: String,
    timeMillis: Long,
    isError: Boolean,
    errorMessage: String,
    context: Context,
    onTimePicked: (Long) -> Unit
) {
    val fmt = remember { SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDateTimePicker(context, timeMillis, onTimePicked) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (isError)
                    AccentRed.copy(alpha = 0.15f)
                else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fmt.format(Date(timeMillis)),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Change",
                    color = AccentYellow,
                    fontSize = 12.sp
                )
            }
        }

        if (isError) {
            Text(
                text = errorMessage,
                color = AccentRed,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AlertTypeCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected)
        Purple40
    else
        PurpleGrey40

    val borderColor = if (isSelected)
        Purple40
    else
        Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon,
                contentDescription = label,
                tint = AccentYellow,
                modifier = Modifier.size(28.dp)
            )
            Text(label,
                fontSize = 12.sp,
                color = AccentYellow,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

fun showDateTimePicker(
    context: Context,
    initialMillis: Long,
    onResult: (Long) -> Unit
) {
    val cal = Calendar.getInstance().apply { timeInMillis = initialMillis }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onResult(picked.timeInMillis)
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis()
    }.show()
}
