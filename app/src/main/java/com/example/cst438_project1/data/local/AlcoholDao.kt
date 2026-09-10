package com.example.cst438_project1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlcoholDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(alcohol: AlcoholEntity)

    @Query("SELECT * FROM Alcohol WHERE id = :alcId LIMIT 1")
    suspend fun findById(alcId: String): AlcoholEntity?

    @Query("SELECT * FROM Alcohol ORDER BY product_name")
    suspend fun getAll(): List<AlcoholEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<AlcoholEntity>)
}
