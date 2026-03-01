package com.example.stormwatch.data.datasource.alert_service

import kotlinx.coroutines.flow.Flow
import com.example.stormwatch.data.model.AlertEntity

class AlertLocalDataSource(private val dao: AlertDao)   {

    fun getAlerts(): Flow<List<AlertEntity>> {
        return dao.getAlerts()
    }

    suspend fun insertAlert(alert: AlertEntity): Long  {
       return dao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        dao.deleteAlert(alert)
    }
}