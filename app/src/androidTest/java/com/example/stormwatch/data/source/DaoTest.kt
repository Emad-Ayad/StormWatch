package com.example.stormwatch.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import com.example.stormwatch.data.datasource.db.MapsDataBase
import com.example.stormwatch.data.datasource.local.FavoritesDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Test
import com.example.stormwatch.data.model.FavoriteEntity
import kotlinx.coroutines.flow.first

class DaoTest {

    private lateinit var database: MapsDataBase
    private lateinit var dao: FavoritesDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MapsDataBase::class.java
        ).build()

        dao = database.favDao()
    }

    @After
    fun close(){
        database.close()
    }

    @Test
    fun insertGetFav() = runTest{
        //set fav
        val fav =  FavoriteEntity(id = 1, cityName = "Alex", lon = 111.0, lat = 111.0)
        // insert into db
        dao.insertFavorite(fav)

        //Then loaded data should contain the expected data
        val expected = dao.getAllFavorites().first().first()
        assertNotNull(expected)
        assertThat(fav, IsEqual(expected))
        assertEquals(expected.id, fav.id)
        assertEquals(expected.cityName , fav.cityName)
    }

    @Test
    fun deleteFav() = runTest {
        //set fav
        val fav = FavoriteEntity(cityName = "Cairo", lon = 33.33, lat = 33.33)
        dao.insertFavorite(fav)

        // delete it
        val inserted = dao.getAllFavorites().first().first()
        dao.deleteFavorite(inserted)

        // list is empty
        val result = dao.getAllFavorites().first()
        assertEquals(0, result.size)
    }

    @Test
    fun getAllFavorites_returnsAllInserted() = runTest {
        // set Favs
        val fav1 = FavoriteEntity(cityName = "Cairo", lon = 33.33, lat = 33.33)
        val fav2 = FavoriteEntity(cityName = "Alex", lon = 44.44, lat = 44.44)
        val fav3 = FavoriteEntity(cityName = "Giza", lon = 55.55, lat = 55.55)
        dao.insertFavorite(fav1)
        dao.insertFavorite(fav2)
        dao.insertFavorite(fav3)

        // insert into db
        val result = dao.getAllFavorites().first()

        // check the results
        assertEquals(3, result.size)
        assertEquals("Cairo", result[0].cityName)
        assertEquals("Alex", result[1].cityName)
        assertEquals("Giza", result[2].cityName)
    }


}