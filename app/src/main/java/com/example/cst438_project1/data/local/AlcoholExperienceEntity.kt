package com.example.cst438_project1.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "Alcohol_Experience",
    primaryKeys = ["user_id", "alc_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        ),
        ForeignKey(
            entity = AlcoholEntity::class,
            parentColumns = ["id"],
            childColumns = ["alc_id"]
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["alc_id"])
    ]
)
data class AlcoholExperienceEntity(
    val user_id: Int,
    val alc_id: String,
    val rating: Int,
    val user_review: String?
) {
    companion object {
        const val MIN_RATING = 1
        const val MAX_RATING = 5
    }

    init {
        require(rating in MIN_RATING..MAX_RATING) {
            "Rating must be between $MIN_RATING and $MAX_RATING."
        }
    }
}
