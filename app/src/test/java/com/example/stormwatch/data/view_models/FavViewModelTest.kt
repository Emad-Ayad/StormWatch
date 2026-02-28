package com.example.stormwatch.data.view_models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stormwatch.data.model.FavoriteEntity
import com.example.stormwatch.data.repo.FavoriteRepository
import com.example.stormwatch.presentation.fav.FavoriteViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: FavoriteRepository
    private lateinit var viewModel: FavoriteViewModel

    private val fakeFavoritesFlow = MutableStateFlow<List<FavoriteEntity>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        every { repo.getFavorites() } returns fakeFavoritesFlow
        viewModel = FavoriteViewModel(repo)
    }

    @Test
    fun addFavorite_callsRepo() = runTest {
        // set fav & insert to db
        viewModel.addFavorite("Cairo", 33.33, 44.44)

        // Then loaded data should contain the expected data
        coVerify { repo.addFavorite("Cairo", 33.33, 44.44) }
    }

    @Test
    fun deleteFavorite_callsRepo() = runTest {
        // set fav
        val fav = FavoriteEntity(id = 1, cityName = "Cairo", lat = 33.33, lon = 44.44)

        // delete fro db
        viewModel.deleteFavorite(fav)

        // Then data should be empty
        coVerify { repo.deleteFavorite(fav) }
    }

    @Test
    fun favorites_fromRepo() = runTest {
        // set favs
        val job = launch {
            viewModel.favorites.collect {}
        }

        val fakeList = listOf(
            FavoriteEntity(id = 1, cityName = "Cairo", lat = 33.33, lon = 44.44),
            FavoriteEntity(id = 2, cityName = "Alex", lat = 55.55, lon = 66.66)
        )

        fakeFavoritesFlow.value = fakeList

        every { repo.getFavorites() } returns fakeFavoritesFlow
        advanceUntilIdle()

        // loaded data should contain the expected data
        assertEquals(2, viewModel.favorites.value.size)
        assertEquals("Cairo", viewModel.favorites.value[0].cityName)
        assertEquals("Alex", viewModel.favorites.value[1].cityName)

        job.cancel()
    }

}