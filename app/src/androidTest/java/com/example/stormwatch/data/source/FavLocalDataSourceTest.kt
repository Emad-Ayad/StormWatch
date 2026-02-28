package com.example.stormwatch.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.stormwatch.data.datasource.db.MapsDataBase
import com.example.stormwatch.data.datasource.local.FavoriteLocalDataSource
import com.example.stormwatch.data.datasource.local.FavoritesDao
import com.example.stormwatch.data.model.FavoriteEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavLocalDataSourceTest {

    private lateinit var database: MapsDataBase
    private lateinit var dao: FavoritesDao
    private lateinit var localDataSource: FavoriteLocalDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MapsDataBase::class.java
        ).build()

        dao = database.favDao()
        localDataSource = FavoriteLocalDataSource(dao)
    }

    @After
    fun close() {
        database.close()
    }
    @Test
    fun insertFavorite_getFavorites() = runTest {
        // set Fav
        val fav = FavoriteEntity(cityName = "Cairo", lat = 30.06, lon = 31.23)

        // insert into db
        localDataSource.insertFavorite(fav)

        // Then loaded data should contain the expected data
        val result = localDataSource.getFavorites().first()
        val expected = result.first()
        assertNotNull(expected)
        assertEquals(fav.cityName, expected.cityName)
        assertEquals(fav.lat, expected.lat)
    }

    @Test
    fun deleteFavorite_notInList() = runTest {
        // set Fav
        val fav = FavoriteEntity(cityName = "Alex", lat = 31.20, lon = 29.92)

        // insert into db
        localDataSource.insertFavorite(fav)

        // delete it
        val inserted = localDataSource.getFavorites().first().first()
        localDataSource.deleteFavorite(inserted)

        // Then this should be empty
        val result = localDataSource.getFavorites().first()
        assertEquals(0, result.size)
    }

    @Test
    fun insertMultipleFavorites_getAllFavorites() = runTest {
        // set Favs
        val fav1 = FavoriteEntity(cityName = "Cairo", lat = 30.06, lon = 31.23)
        val fav2 = FavoriteEntity(cityName = "Alex", lat = 31.20, lon = 29.92)
        val fav3 = FavoriteEntity(cityName = "Giza", lat = 30.01, lon = 31.13)

        // insert into db
        localDataSource.insertFavorite(fav1)
        localDataSource.insertFavorite(fav2)
        localDataSource.insertFavorite(fav3)

        // Then loaded data should contain the expected data
        val result = localDataSource.getFavorites().first()
        assertThat(result.size, IsEqual(3))
        assertEquals("Cairo", result[0].cityName)
        assertEquals("Alex", result[1].cityName)
        assertEquals("Giza", result[2].cityName)
    }
}