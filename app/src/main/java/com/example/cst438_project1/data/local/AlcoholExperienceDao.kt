package com.example.cst438_project1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlcoholExperienceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(experience: AlcoholExperienceEntity)

    @Query("SELECT * FROM Alcohol_Experience WHERE user_id = :userId AND alc_id = :alcId LIMIT 1")
    suspend fun get(userId: Int, alcId: String): AlcoholExperienceEntity?
}
