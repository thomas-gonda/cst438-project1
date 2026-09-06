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

    @Query("SELECT COUNT(*) FROM Alcohol_Record WHERE user_id = :userId AND alc_id = :alcId")
    suspend fun getTimesConsumed(userId: Int, alcId: String): Int

    @Query("SELECT MAX(date) FROM Alcohol_Record WHERE user_id = :userId AND alc_id = :alcId")
    suspend fun getLastConsumedDate(userId: Int, alcId: String): String?
}
