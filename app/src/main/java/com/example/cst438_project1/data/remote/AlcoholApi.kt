package com.example.cst438_project1.data.remote

import android.util.Log
import com.example.cst438_project1.data.model.Alcohol
import com.example.cst438_project1.data.model.Nutriments
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

object AlcoholApi {
    private const val TAG = "AlcoholApi"
    private const val OPEN_FOOD_FACTS_URL = "https://world.openfoodfacts.org/cgi/search.pl"
    private const val LCBO_GRAPHQL_URL = "https://api.lcbo.dev/graphql"

    /** Searches Open Food Facts first, then LCBO if it fails or finds no match. */
    suspend fun getAlcohol(name: String, resultCount: Int = 1): List<Alcohol> = withContext(Dispatchers.IO) {
        if (name.isBlank()) return@withContext emptyList()
        require(resultCount > 0) { "resultCount must be greater than 0." }

        try {
            val products = getFromOpenFoodFacts(name, resultCount)
            if (products.isNotEmpty()) {
                Log.i(TAG, "Open Food Facts returned ${products.size} result(s) for '$name'.")
                return@withContext products
            }
            Log.i(TAG, "Open Food Facts returned no matches for '$name'; trying LCBO.")
        } catch (error: Exception) {
            Log.w(TAG, "Open Food Facts failed for '$name'; trying LCBO instead.", error)
        }

        val products = getFromLcbo(name, resultCount)
        Log.i(TAG, "LCBO returned ${products.size} result(s) for '$name'.")
        products
    }

    private fun getFromOpenFoodFacts(name: String, resultCount: Int): List<Alcohol> {
        val searchText = URLEncoder.encode(name.trim(), "UTF-8")
        val url = URL(
            "$OPEN_FOOD_FACTS_URL?json=1&action=process&search_simple=1" +
                    "&search_terms=$searchText&tagtype_0=categories&tag_contains_0=contains" +
                    "&tag_0=Alcoholic%20beverages&page_size=$resultCount" +
                    "&fields=code,product_name,brands,categories,countries,quantity,image_url,nutriments"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        try {
            check(connection.responseCode in 200..299) {
                "Open Food Facts returned HTTP ${connection.responseCode}"
            }
            val response = Gson().fromJson(
                connection.inputStream.bufferedReader().use { it.readText() },
                AlcoholSearchResponse::class.java
            )
            return response.products.take(resultCount)
        } finally {
            connection.disconnect()
        }
    }

    private fun getFromLcbo(name: String, resultCount: Int): List<Alcohol> {
        val query = """
            query ProductSearch(${'$'}search: String!, ${'$'}first: Int!) {
              products(filters: { search: ${'$'}search }, pagination: { first: ${'$'}first }) {
                edges {
                  node {
                    sku name primaryCategory producerName origin countryOfManufacture
                    thumbnailUrl upcNumber alcoholPercent unitVolumeMl
                  }
                }
              }
            }
        """.trimIndent()
        val requestBody = Gson().toJson(
            mapOf(
                "query" to query,
                "variables" to mapOf("search" to name.trim(), "first" to resultCount)
            )
        )
        val connection = (URL(LCBO_GRAPHQL_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        try {
            connection.outputStream.use { it.write(requestBody.toByteArray(Charsets.UTF_8)) }
            check(connection.responseCode in 200..299) {
                "LCBO returned HTTP ${connection.responseCode}"
            }
            val root = JsonParser.parseString(
                connection.inputStream.bufferedReader().use { it.readText() }
            ).asJsonObject
            check(!root.has("errors")) { "LCBO returned a GraphQL error" }

            val edges = root.getAsJsonObject("data")
                ?.getAsJsonObject("products")
                ?.getAsJsonArray("edges")
                ?: return emptyList()
            return edges.map { edge ->
                lcboProductToAlcohol(edge.asJsonObject.getAsJsonObject("node"))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun lcboProductToAlcohol(product: JsonObject): Alcohol = Alcohol(
        barcode = product.stringOrNull("upcNumber") ?: product.stringOrNull("sku"),
        name = product.stringOrNull("name"),
        brand = product.stringOrNull("producerName"),
        category = product.stringOrNull("primaryCategory"),
        countries = product.stringOrNull("countryOfManufacture") ?: product.stringOrNull("origin"),
        size = product.intOrNull("unitVolumeMl")?.let { "$it ml" },
        imageUrl = product.stringOrNull("thumbnailUrl"),
        nutriments = product.doubleOrNull("alcoholPercent")?.let(::Nutriments)
    )

    private fun JsonObject.stringOrNull(field: String): String? =
        get(field)?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() }

    private fun JsonObject.intOrNull(field: String): Int? =
        get(field)?.takeUnless { it.isJsonNull }?.asInt

    private fun JsonObject.doubleOrNull(field: String): Double? =
        get(field)?.takeUnless { it.isJsonNull }?.asDouble

    private data class AlcoholSearchResponse(val products: List<Alcohol> = emptyList())
}