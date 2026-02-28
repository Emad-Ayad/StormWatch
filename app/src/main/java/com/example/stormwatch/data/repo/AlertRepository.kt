package com.example.stormwatch.data.repo

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.stormwatch.data.datasource.alert_service.AlertLocalDataSource
import kotlinx.coroutines.flow.Flow
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.datasource.alert_service.AlertWorker
import java.util.concurrent.TimeUnit

class AlertRepository(
    private val localDataSource: AlertLocalDataSource,
    private val context: Context
) {

    fun getAlerts(): Flow<List<AlertEntity>> =
        localDataSource.getAlerts()

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

        localDataSource.insertAlert(alert)
        scheduleWork(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
        WorkManager.getInstance(context)
            .cancelUniqueWork("alert_${alert.id}")
    }

    private fun scheduleWork(alert: AlertEntity) {
        val delay = alert.startTime - System.currentTimeMillis()

        val data = workDataOf(
            "lat" to alert.lat,
            "lon" to alert.lon,
            "city" to alert.city,
            "type" to alert.alertType,
            "endTime" to alert.endTime
        )

        val request = PeriodicWorkRequestBuilder<AlertWorker>(
            15, TimeUnit.MINUTES
        ).setInitialDelay(if (delay > 0) delay else 0L, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(alert.id.toString())
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "alert_${alert.id}",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}