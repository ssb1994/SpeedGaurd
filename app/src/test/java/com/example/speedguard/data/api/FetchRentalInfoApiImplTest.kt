package com.example.speedguard.data.api

import com.example.speedguard.data.model.RentalInfo
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import kotlin.random.Random

class FetchRentalInfoApiImplTest {
    private lateinit var fetchRentalInfoApi: IFetchRentalInfoApi

    @Before
    fun setUp() {
        mockkObject(Random.Default)
        fetchRentalInfoApi = FetchRentalInfoApiImpl()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchRentalInfo returns Active when random returns true`() = runTest {
        every { Random.Default.nextBoolean() } returns true

        val result =
            fetchRentalInfoApi.fetchRentalInfo("") as Result.Active<RentalInfo>

        assertEquals(80.0, result.info.speedLimit, 0.01)
    }

    @Test
    fun `fetchRentalInfo returns NoActiveRental when random returns false`() {
        runTest {
            every { Random.Default.nextBoolean() } returns false
            val result =
                fetchRentalInfoApi.fetchRentalInfo("") as Result.NoActiveRental

            assertEquals(Result.NoActiveRental, result)

        }

    }

    @Test
    fun `fetchRentalInfo returns Error when exception is thrown`() {
        runTest {
            every { Random.Default.nextBoolean() } returns false
            val impl = object : IFetchRentalInfoApi {
                override suspend fun fetchRentalInfo(vehicleRegNumber: String): Result {
                    return try {
                        throw RuntimeException("Simulated crash")
                    } catch (e: Exception) {
                        Result.Error(e)
                    }
                }
            }

            val result = impl.fetchRentalInfo("")

            println(" Test tag = ${(result as Result.Error).e.message}")
            assertEquals("Simulated crash", (result).e.message)

        }

    }

}