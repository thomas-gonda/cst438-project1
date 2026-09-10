package com.example.cst438_project1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        AlcoholEntity::class,
        AlcoholExperienceEntity::class,
        AlcoholRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun alcoholDao(): AlcoholDao
    abstract fun alcoholExperienceDao(): AlcoholExperienceDao
    abstract fun alcoholRecordDao(): AlcoholRecordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alcohol_tracker.db"
                ).build().also { instance = it }
            }
    }
}
