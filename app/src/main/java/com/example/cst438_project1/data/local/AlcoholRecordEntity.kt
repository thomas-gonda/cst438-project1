package com.example.cst438_project1.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Alcohol_Record",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AlcoholEntity::class, parentColumns = ["id"], childColumns = ["alc_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("user_id"), Index("alc_id"), Index("date")]
)
data class AlcoholRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val user_id: Int,
    val alc_id: String,
    // ISO format YYYY-MM-DD keeps alphabetical and chronological order identical.
    val date: String
)
