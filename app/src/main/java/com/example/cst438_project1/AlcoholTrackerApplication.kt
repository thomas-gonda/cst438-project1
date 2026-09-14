package com.example.cst438_project1

import android.app.Application
import com.example.cst438_project1.data.local.AppDatabase
import com.example.cst438_project1.data.local.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlcoholTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this)

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            DatabaseSeeder.seedIfNeeded(this@AlcoholTrackerApplication, database)
            DatabaseSeeder.seedUsers(this@AlcoholTrackerApplication, database)
        }
    }
}
