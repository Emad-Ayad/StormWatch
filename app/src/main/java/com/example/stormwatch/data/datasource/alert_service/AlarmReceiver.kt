package com.example.stormwatch.data.datasource.alert_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.repo.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALERT_CHANNEL = "weather_alert_channel"
        const val ALARM_CHANNEL = "weather_alarm_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val city = intent.getStringExtra("city") ?: return
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val type = intent.getStringExtra("type") ?: "NOTIFICATION"
        val endTime = intent.getLongExtra("endTime", 0L)

        if (System.currentTimeMillis() > endTime) return


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remoteDataSource = RemoteDataSource()
                val repo = WeatherRepository(remoteDataSource)
                val weather = repo.getForecast(lat, lon, "metric", "en")
                val condition = weather.list.firstOrNull()
                    ?.weather?.firstOrNull()?.description ?: "Unknown"
                val temp = weather.list.firstOrNull()?.main?.temp ?: 0.0

                val isBadWeather = condition.contains("rain", ignoreCase = true) ||
                        condition.contains("storm", ignoreCase = true) ||
                        condition.contains("snow", ignoreCase = true) ||
                        condition.contains("thunder", ignoreCase = true) ||
                        condition.contains("drizzle", ignoreCase = true) ||
                        condition.contains("hail", ignoreCase = true) ||
                        temp < 0.0 ||true

                if (isBadWeather) {
                    createChannels(context)
                    if (type == "ALARM") {
                        showAlarmNotification(context, city, condition)
                    } else {
                        showNotification(context, city, condition)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifChannel = NotificationChannel(
                ALERT_CHANNEL, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Weather condition alert notifications" }

            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL, "Weather Alarm Sounds", NotificationManager.IMPORTANCE_HIGH
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

    private fun showNotification(context: Context, city: String, condition: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("Weather Alert — $city")
            .setContentText("Current condition: $condition")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Bad weather detected in $city.\nCondition: $condition\nStay safe.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showAlarmNotification(context: Context, city: String, condition: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("Weather Alarm — $city")
            .setContentText("Current condition: $condition")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Severe weather in $city!\nCondition: $condition\nTake immediate precautions.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}