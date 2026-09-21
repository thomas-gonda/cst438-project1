package com.example.cst438_project1.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AlcoholApiTest {

    @Test
    fun searchingForAbsolutVodkaReturnsMatchingProduct() = runBlocking {
        val result = try {
            AlcoholApi.getAlcohol("Absolut Vodka")
        } catch (error: IOException) {
            // The API is external and may return HTTP 500 or be unavailable.
            return@runBlocking
        }

        assertEquals(1, result.size)

        val alcohol = result.first()

        assertTrue(
            "Expected a product name containing Absolut, but was ${alcohol.name}",
            alcohol.name?.contains("Absolut", ignoreCase = true) == true
        )
        assertTrue(
            "Expected a barcode for ${alcohol.name}",
            !alcohol.barcode.isNullOrBlank()
        )
        assertTrue(
            "Expected a category for ${alcohol.name}",
            !alcohol.category.isNullOrBlank()
        )
    }

    @Test
    fun resultCountLimitsReturnedProducts() = runBlocking {
        val result = try {
            AlcoholApi.getAlcohol("beer", resultCount = 2)
        } catch (error: IOException) {
            // The API is external and may return HTTP 500 or be unavailable.
            return@runBlocking
        }

        assertEquals(2, result.size)
        assertTrue(result.all { !it.name.isNullOrBlank() })
    }

    @Test
    fun blankNameReturnsEmptyList() = runBlocking {
        val result =
            AlcoholApi.getAlcohol("   ")

        assertTrue(result.isEmpty())
    }

    @Test
    fun zeroResultCountIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                AlcoholApi.getAlcohol("vodka", 0)
            }
        }
    }

    @Test
    fun negativeResultCountIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                AlcoholApi.getAlcohol("vodka", -1)
            }
        }
    }
}
