package com.example.cst438_project1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlcoholRecordDao {
    @Insert
    suspend fun insert(record: AlcoholRecordEntity): Long

    @Query("SELECT * FROM Alcohol_Record WHERE user_id = :userId ORDER BY date DESC, id DESC")
    suspend fun getTimeline(userId: Int): List<AlcoholRecordEntity>

    @Query("SELECT * FROM Alcohol_Record WHERE user_id = :userId AND alc_id = :alcId ORDER BY date DESC, id DESC")
    suspend fun getForAlcohol(userId: Int, alcId: String): List<AlcoholRecordEntity>

    @Query("SELECT COUNT(*) FROM Alcohol_Record WHERE user_id = :userId AND alc_id = :alcId")
    suspend fun getTimesConsumed(userId: Int, alcId: String): Int

    @Query("SELECT MAX(date) FROM Alcohol_Record WHERE user_id = :userId AND alc_id = :alcId")
    suspend fun getLastConsumedDate(userId: Int, alcId: String): String?

    @Query(
        """
    SELECT
        record.id AS record_id,
        record.user_id AS user_id,
        record.alc_id AS alcohol_id,
        alcohol.product_name AS product_name,
        alcohol.brand AS brand,
        alcohol.abv AS abv,
        alcohol.image_url AS image_url,
        record.date AS date
    FROM Alcohol_Record AS record
    INNER JOIN Alcohol AS alcohol
        ON alcohol.id = record.alc_id
    WHERE record.user_id = :userId
    ORDER BY record.date DESC, record.id DESC
    """
    )
    suspend fun getTimelineEntries(
        userId: Int
    ): List<AlcoholTimelineEntry>
}
