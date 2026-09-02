package com.example.cst438_project1.data.remote

import com.example.cst438_project1.data.model.Alcohol
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

object AlcoholApi {

    private const val BASE_URL =
        "https://world.openfoodfacts.org/cgi/search.pl"

    suspend fun getAlcohol(name: String): Alcohol? = withContext(Dispatchers.IO) {
        if (name.isBlank()) return@withContext null

        val searchText = URLEncoder.encode(name.trim(), "UTF-8")

        val url = URL(
            "$BASE_URL" +
                    "?json=1" +
                    "&action=process" +
                    "&search_simple=1" +
                    "&search_terms=$searchText" +
                    "&tagtype_0=categories" +
                    "&tag_contains_0=contains" +
                    "&tag_0=Alcoholic%20beverages" +
                    "&page_size=1" +
                    "&fields=code,product_name,brands,categories,countries,quantity,image_url,nutriments"
        )

        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            if (connection.responseCode !in 200..299) {
                return@withContext null
            }

            val responseJson = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val response = Gson().fromJson(
                responseJson,
                AlcoholSearchResponse::class.java
            )

            response.products.firstOrNull()
        } finally {
            connection.disconnect()
        }
    }

    private data class AlcoholSearchResponse(
        val products: List<Alcohol> = emptyList()
    )
}