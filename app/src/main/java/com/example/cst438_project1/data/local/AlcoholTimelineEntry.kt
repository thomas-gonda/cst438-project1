package com.example.cst438_project1.data.local

import androidx.room.ColumnInfo

data class AlcoholTimelineEntry(
    @ColumnInfo(name = "record_id")
    val recordId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "alcohol_id")
    val alcoholId: String,

    @ColumnInfo(name = "product_name")
    val productName: String,

    val brand: String?,

    val abv: Double?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    val date: String,

    val rating: Int?,

    @ColumnInfo(name = "user_review")
    val userReview: String?
)
