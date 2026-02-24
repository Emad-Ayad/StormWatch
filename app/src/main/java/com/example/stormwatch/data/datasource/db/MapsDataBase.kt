package com.example.stormwatch.data.datasource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stormwatch.data.model.FavoriteEntity
import com.example.stormwatch.data.datasource.local.FavoritesDao


@Database(entities = arrayOf(FavoriteEntity::class), version = 1,exportSchema = false)
abstract class MapsDataBase  : RoomDatabase() {

    abstract fun favDao() : FavoritesDao

    companion object {
        private var INSTANCE: MapsDataBase? = null

        fun getInstance(ctx: Context): MapsDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    MapsDataBase::class.java,
                    "maps_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}