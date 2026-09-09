package com.example.cst438_project1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alcohol")
data class AlcoholEntity(
    @PrimaryKey val id: String,
    val product_name: String,
    val brand: String?,
    val countries: String?,
    val abv: Double?,
    val image_url: String?
)
