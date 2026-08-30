package com.example.cst438_project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.ui.theme.Cst438project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AlcoholProduct(
    val sku: String,
    val name: String,
    val priceInCents: Int,
    val producerName: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Cst438project1Theme {
                LcboProductScreen()
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun LcboProductScreen() {
    var products by remember { mutableStateOf<List<AlcoholProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "LCBO Alcohol Products",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            products = withContext(Dispatchers.IO) {
                                LcboApi.getProducts()
                            }
                        } catch (e: Exception) {
                            errorMessage = "Could not load products: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Load products")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Producer: ${product.producerName}")
                            Text("Price: $%.2f".format(product.priceInCents / 100.0))
                            Text("SKU: ${product.sku}")
                        }
                    }
                }
            }
        }
    }
}

object LcboApi {
    private const val API_URL = "https://api.lcbo.dev/graphql"

    fun getProducts(): List<AlcoholProduct> {
        val query = """
            {
              products(pagination: { first: 5 }) {
                edges {
                  node {
                    sku
                    name
                    priceInCents
                    producerName
                  }
                }
              }
            }
        """.trimIndent()

        val connection = URL(API_URL).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val requestBody = JSONObject()
            .put("query", query)
            .toString()

        connection.outputStream.use {
            it.write(requestBody.toByteArray())
        }

        if (connection.responseCode !in 200..299) {
            throw Exception("Server returned HTTP ${connection.responseCode}")
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }

        val edges = JSONObject(response)
            .getJSONObject("data")
            .getJSONObject("products")
            .getJSONArray("edges")

        return List(edges.length()) { index ->
            val node = edges.getJSONObject(index).getJSONObject("node")

            AlcoholProduct(
                sku = node.getString("sku"),
                name = node.getString("name"),
                priceInCents = node.getInt("priceInCents"),
                producerName = node.optString("producerName", "Unknown")
            )
        }
    }
}