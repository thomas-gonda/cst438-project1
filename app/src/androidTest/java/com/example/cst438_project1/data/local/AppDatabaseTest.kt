package com.example.cst438_project1.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDatabase() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveAndLoadAlcohol() = runBlocking {
        val alcohol = AlcoholEntity(
            id = "7312040017683",
            product_name = "Absolut Vodka",
            brand = "Absolut",
            countries = "Sweden",
            abv = 40.0,
            image_url = "https://example.com/absolut.jpg"
        )

        database.alcoholDao().save(alcohol)

        val savedAlcohol =
            database.alcoholDao().findById(alcohol.id)

        assertEquals(alcohol, savedAlcohol)
    }

    @Test
    fun duplicateAlcoholIdReplacesExistingProduct() = runBlocking {
        val original = AlcoholEntity(
            id = "12345",
            product_name = "Original product",
            brand = null,
            countries = "Canada",
            abv = 35.0,
            image_url = null
        )

        val updated = original.copy(
            product_name = "Updated product",
            brand = "Updated brand",
            image_url = "https://example.com/image.jpg"
        )

        database.alcoholDao().save(original)
        database.alcoholDao().save(updated)

        val products = database.alcoholDao().getAll()

        assertEquals(1, products.size)
        assertEquals(updated, products.first())
    }

    @Test
    fun insertAndFindUser() = runBlocking {
        val userId = database.userDao().insert(
            UserEntity(
                first_name = "Linus",
                last_name = "Schaub",
                password = "test-password",
                username = "lschaubcsumb"
            )
        )

        val savedUser =
            database.userDao().findByUsername("lschaubcsumb")

        assertNotNull(savedUser)
        assertEquals(userId.toInt(), savedUser?.id)
        assertEquals("Linus", savedUser?.first_name)
        assertEquals("Schaub", savedUser?.last_name)
    }

    @Test
    fun unknownUsernameReturnsNull() = runBlocking {
        val result =
            database.userDao().findByUsername("does-not-exist")

        assertNull(result)
    }

    @Test
    fun saveRatingAndReview() = runBlocking {
        val userId = insertTestUser()
        val alcohol = insertTestAlcohol()

        val experience = AlcoholExperienceEntity(
            user_id = userId,
            alc_id = alcohol.id,
            rating = 5,
            user_review = "Excellent vodka"
        )

        database.alcoholExperienceDao().save(experience)

        val savedExperience =
            database.alcoholExperienceDao().get(
                userId = userId,
                alcId = alcohol.id
            )

        assertEquals(experience, savedExperience)
    }

    @Test
    fun ratingOutsideAllowedRangeIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            AlcoholExperienceEntity(
                user_id = 1,
                alc_id = "12345",
                rating = 6,
                user_review = null
            )
        }
    }

    @Test
    fun recordsStoreEveryConsumptionSeparately() = runBlocking {
        val userId = insertTestUser()
        val alcohol = insertTestAlcohol()

        database.alcoholRecordDao().insert(
            AlcoholRecordEntity(
                user_id = userId,
                alc_id = alcohol.id,
                date = "2026-01-10"
            )
        )

        database.alcoholRecordDao().insert(
            AlcoholRecordEntity(
                user_id = userId,
                alc_id = alcohol.id,
                date = "2026-02-15"
            )
        )

        database.alcoholRecordDao().insert(
            AlcoholRecordEntity(
                user_id = userId,
                alc_id = alcohol.id,
                date = "2025-12-30"
            )
        )

        val timeline =
            database.alcoholRecordDao().getTimeline(userId)

        val count =
            database.alcoholRecordDao()
                .getTimesConsumed(userId, alcohol.id)

        val lastDate =
            database.alcoholRecordDao()
                .getLastConsumedDate(userId, alcohol.id)

        assertEquals(3, timeline.size)
        assertEquals("2026-02-15", timeline[0].date)
        assertEquals("2026-01-10", timeline[1].date)
        assertEquals("2025-12-30", timeline[2].date)
        assertEquals(3, count)
        assertEquals("2026-02-15", lastDate)
    }

    private suspend fun insertTestUser(): Int {
        return database.userDao().insert(
            UserEntity(
                first_name = "Test",
                last_name = "User",
                password = "password",
                username = "test-user"
            )
        ).toInt()
    }

    private suspend fun insertTestAlcohol(): AlcoholEntity {
        val alcohol = AlcoholEntity(
            id = "7312040017683",
            product_name = "Absolut Vodka",
            brand = "Absolut",
            countries = "Sweden",
            abv = 40.0,
            image_url = "https://example.com/absolut.jpg"
        )

        database.alcoholDao().save(alcohol)
        return alcohol
    }
}