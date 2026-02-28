package com.example.stormwatch.data.datasource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stormwatch.data.model.FavoriteEntity
import com.example.stormwatch.data.datasource.local.FavoritesDao
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.datasource.alert_service.AlertDao

@Database(entities = arrayOf(FavoriteEntity::class, AlertEntity::class), version = 2,exportSchema = false)
abstract class MapsDataBase  : RoomDatabase() {

    abstract fun favDao() : FavoritesDao
    abstract fun alertDao(): AlertDao

    companion object {
        private var INSTANCE: MapsDataBase? = null

        fun getInstance(ctx: Context): MapsDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                ctx.applicationContext,
                                MapsDataBase::class.java,
                                "maps_database"
                            ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}