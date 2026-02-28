package com.example.stormwatch.data.repo

import com.example.stormwatch.data.datasource.remote.RemoteDataSource
import com.example.stormwatch.data.model.ForecastResponse
import com.example.stormwatch.data.model.GeoCodingDto
import com.example.stormwatch.data.model.ReverseGeoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepoTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repo: WeatherRepository

    @Before
    fun setup() {
        remoteDataSource = mockk(relaxed = true)
        repo = WeatherRepository(remoteDataSource)
    }

    @Test
    fun getForecast_returnsDataFromRemote() = runTest {
        // set fake data
        val fakeForecast = mockk<ForecastResponse>(relaxed = true)
        coEvery {
            remoteDataSource.getForecast(33.0, 34.0, "metric", "en")
        } returns fakeForecast

        // get forecast
        val result = repo.getForecast(33.0, 34.0, "metric", "en")

        // Then loaded data should contain the expected data
        assertEquals(fakeForecast, result)
        coVerify {
            remoteDataSource.getForecast(33.0, 34.0, "metric", "en")
        }
    }

    @Test
    fun searchCity_returnsListFromRemote() = runTest {
        // set fake data
        val fakeCities = listOf(
            GeoCodingDto(name = "Cairo", country = "EG", lat = 30.0, lon = 33.33),
            GeoCodingDto(name = "Tokyo", country = "jP", lat = 55.55, lon = 66.66)
        )
        coEvery { remoteDataSource.searchCity("Cairo") } returns fakeCities

        // search city
        val result = repo.searchCity("Cairo")

        // Then loaded data should contain the expected data
        assertEquals(2, result.size)
        assertEquals("Cairo", result[0].name)
        assertEquals("EG", result[0].country)
        coVerify(exactly = 1) { remoteDataSource.searchCity("Cairo") }
    }

    @Test
    fun reverseGeocode_returnsLocationFromRemote() = runTest {
        // set fake data
        val fakeLocation = ReverseGeoDto(name = "Cairo", country = "EG")
        coEvery { remoteDataSource.reverseGeocode(33.0, 44.0) } returns fakeLocation

        // get city
        val result = repo.reverseGeocode(33.0, 44.0)

        // Then loaded data should contain the expected data
        assertEquals("Cairo", result?.name)
        assertEquals("EG", result?.country)
        coVerify(exactly = 1) { remoteDataSource.reverseGeocode(33.0, 44.0) }
    }

}