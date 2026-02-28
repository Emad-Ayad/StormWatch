package com.example.stormwatch.data.datasource.alert_service

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.repo.WeatherRepository

class AlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val ALERT_CHANNEL = "weather_alert_channel"
        const val ALARM_CHANNEL = "weather_alarm_channel"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val lat = inputData.getDouble("lat", 0.0)
        val lon = inputData.getDouble("lon", 0.0)
        val city = inputData.getString("city") ?: ""
        val type = inputData.getString("type") ?: "NOTIFICATION"
        val endTime = inputData.getLong("endTime", 0L)

        if (System.currentTimeMillis() > endTime) {
            return Result.success()
        }

        return try {
            val remoteDataSource = RemoteDataSource()
            val repo = WeatherRepository(remoteDataSource)
            val weather = repo.getForecast(lat, lon, "metric", "en")
            val condition = weather.list.firstOrNull()?.weather?.firstOrNull()?.description ?: "Unknown"

            val isBadWeather = condition.contains("rain", ignoreCase = true) ||
                    condition.contains("storm", ignoreCase = true) ||
                    condition.contains("snow", ignoreCase = true) ||
                    condition.contains("thunder", ignoreCase = true) ||
                    condition.contains("drizzle", ignoreCase = true) ||
                    condition.contains("hail", ignoreCase = true) ||
                    true


            if (isBadWeather) {
                createChannels()
                if (type == "ALARM") {
                    showAlarmNotification(city, condition)
                } else {
                    showNotification(city, condition)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            val notifChannel = NotificationChannel(
                ALERT_CHANNEL,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather condition alert notifications"
            }

            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL,
                "Weather Alarm Sounds",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alarm notifications with sound"
                setSound(alarmUri, audioAttr)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            manager.createNotificationChannel(notifChannel)
            manager.createNotificationChannel(alarmChannel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(city: String, condition: String) {
        val notification = NotificationCompat.Builder(applicationContext, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("Weather Alert — $city")
            .setContentText("Current condition: $condition")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Bad weather detected in $city.\nCondition: $condition\nTake Care."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showAlarmNotification(city: String, condition: String) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(applicationContext, ALARM_CHANNEL)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("Weather Alarm — $city")
            .setContentText("Current condition: $condition")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Bad weather detected in $city!\nCondition: $condition\nTake Care."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
