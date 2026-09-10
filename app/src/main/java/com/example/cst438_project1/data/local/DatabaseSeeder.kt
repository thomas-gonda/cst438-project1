package com.example.cst438_project1.data.local

import android.content.Context
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object DatabaseSeeder {
    suspend fun seedIfNeeded(context: Context, database: AppDatabase) =
        withContext(Dispatchers.IO) {
            val json = context.assets.open("alcohol_seed.json")
                .bufferedReader()
                .use { it.readText() }

            val products = JSONArray(json).let { array ->
                (0 until array.length()).map { index ->
                    val item = array.getJSONObject(index)

                    AlcoholEntity(
                        id = item.getString("id"),
                        product_name = item.getString("product_name"),
                        brand = item.optString("brand").takeIf { it.isNotBlank() },
                        countries = item.optString("countries").takeIf { it.isNotBlank() },
                        abv = if (item.isNull("abv")) null else item.getDouble("abv"),
                        image_url = item.optString("image_url").takeIf { it.isNotBlank() }
                    )
                }
            }

            database.withTransaction {
                database.alcoholDao().insertAll(products)
            }
        }

    suspend fun seedUsers(context: Context, database: AppDatabase) =
        withContext(Dispatchers.IO) {
            val json = context.assets.open("user_seed.json")
                .bufferedReader()
                .use { it.readText() }

            val users = JSONArray(json).let { array ->
                (0 until array.length()).map { index ->
                    val item = array.getJSONObject(index)

                    UserEntity(
                        first_name = item.getString("first_name"),
                        last_name = item.getString("last_name"),
                        password = item.getString("password"),
                        username = item.getString("username")
                    )
                }
            }

            database.withTransaction {
                database.userDao().insertAll(users)
            }
        }
}