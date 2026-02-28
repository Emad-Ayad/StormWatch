package com.example.stormwatch.data.view_models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stormwatch.data.model.AlertEntity
import com.example.stormwatch.data.model.FavoriteEntity
import com.example.stormwatch.data.repo.AlertRepository
import com.example.stormwatch.presentation.alert.view_model.AlertViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
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
class AlertViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: AlertRepository
    private lateinit var viewModel: AlertViewModel

    private val fakeAlertsFlow = MutableStateFlow<List<AlertEntity>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        every { repo.getAlerts() } returns fakeAlertsFlow
        viewModel = AlertViewModel(repo)
    }

    @Test
    fun addAlert_callsRepo() = runTest {
        // set alert & insert to db
        viewModel.addAlert(city = "Cairo", lat = 33.33, lon = 44.44,
            start = 1000L, end = 2000L, type = "NOTIFICATION")

        // Then loaded data should contain the expected data
        coVerify { repo.addAlert(city = "Cairo", lat = 33.33, lon = 44.44,
            startTime = 1000L, endTime = 2000L, alertType = "NOTIFICATION") }
    }

    @Test
    fun deleteAlert_callsRepo() = runTest {
        // set alert
        val alert = AlertEntity(id = 1, city = "Cairo", lat = 33.33, lon = 44.44,
            startTime = 1000L, endTime = 2000L, alertType = "NOTIFICATION")

        // delete fro db
        viewModel.deleteAlert(alert)

        // Then data should be empty
        coVerify { repo.deleteAlert(alert) }
    }

    @Test
    fun alerts_fromRepo() = runTest {
        // set fake alarms
        val job = launch { viewModel.alerts.collect {} }

        val fakeList = listOf(
            AlertEntity(id = 1, city = "Cairo", lat = 33.33, lon = 44.44,
                startTime = 1000L, endTime = 2000L, alertType = "NOTIFICATION"),

            AlertEntity(id = 2, city = "Alex", lat = 55.55, lon = 66.66,
                startTime = 1000L, endTime = 2000L, alertType = "ALARM")
        )

        fakeAlertsFlow.value = fakeList
        every { repo.getAlerts() } returns fakeAlertsFlow
        advanceUntilIdle()


        // Then loaded data should contain the expected data
        assertEquals(2, viewModel.alerts.value.size)
        assertEquals("Cairo", viewModel.alerts.value[0].city)
        assertEquals("Alex", viewModel.alerts.value[1].city)

        job.cancel()
    }

}