package com.example.cst438_project1.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "Alcohol_Experience",
    primaryKeys = ["user_id", "alc_id"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AlcoholEntity::class, parentColumns = ["id"], childColumns = ["alc_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("user_id"), Index("alc_id")]
)
data class AlcoholExperienceEntity(
    val user_id: Int,
    val alc_id: String,
    val rating: Int,
    val user_review: String?
) {
    init {
        require(rating in 1..5) { "Rating must be between 1 and 5." }
    }
}
