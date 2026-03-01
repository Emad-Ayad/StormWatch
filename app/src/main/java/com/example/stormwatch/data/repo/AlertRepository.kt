package com.example.stormwatch.data.repo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.stormwatch.data.datasource.alert_service.AlertLocalDataSource
import kotlinx.coroutines.flow.Flow
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.datasource.alert_service.AlarmReceiver


class AlertRepository(
    private val localDataSource: AlertLocalDataSource,
    private val context: Context
) {

    fun getAlerts(): Flow<List<AlertEntity>> = localDataSource.getAlerts()

    suspend fun addAlert(
        lat: Double,
        lon: Double,
        city: String,
        startTime: Long,
        endTime: Long,
        alertType: String
    ) {
        val alert = AlertEntity(
            lat = lat,
            lon = lon,
            city = city,
            startTime = startTime,
            endTime = endTime,
            alertType = alertType
        )
        val alarmId = localDataSource.insertAlert(alert)

        val inserted =  alert.copy(id = alarmId.toInt())
        scheduleAlarm(inserted)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
        cancelAlarm(alert)
    }

    private fun scheduleAlarm(alert: AlertEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("lat", alert.lat)
            putExtra("lon", alert.lon)
            putExtra("city", alert.city)
            putExtra("type", alert.alertType)
            putExtra("endTime", alert.endTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, alert.id,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alert.startTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alert.startTime,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm(alert: AlertEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alert.id,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

}