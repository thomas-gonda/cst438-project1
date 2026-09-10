package com.example.cst438_project1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.data.local.AlcoholEntity
import com.example.cst438_project1.data.local.AppDatabase
import com.example.cst438_project1.data.model.Alcohol
import com.example.cst438_project1.data.remote.AlcoholApi
import com.example.cst438_project1.ui.theme.Cst438project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cst438project1Theme {
                AlcoholSearchScreen()
            }
        }
    }
}

@Composable
fun AlcoholSearchScreen() {
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AlcoholEntity>>(emptyList()) }
    var selectedAlcohol by remember { mutableStateOf<AlcoholEntity?>(null) }
    var apiResults by remember { mutableStateOf<List<Alcohol>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val database = remember {
        AppDatabase.getInstance(context)
    }

    // Search the local Room database whenever the text changes.
    LaunchedEffect(searchText, selectedAlcohol) {
        if (selectedAlcohol != null || searchText.isBlank()) {
            suggestions = emptyList()
        } else {
            suggestions = withContext(Dispatchers.IO) {
                database.alcoholDao()
                    .getAll()
                    .filter { product ->
                        product.product_name.contains(
                            searchText.trim(),
                            ignoreCase = true
                        )
                    }
                    .sortedBy { it.product_name.lowercase() }
                    .take(5)
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Alcohol search",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                    selectedAlcohol = null
                    apiResults = emptyList()
                    errorMessage = null
                },
                label = { Text("Search alcohols") },
                placeholder = { Text("Type a product name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Button(
                onClick = {
                    val query = searchText.trim()

                    if (query.isEmpty()) {
                        errorMessage = "Enter an alcohol name to search."
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            selectedAlcohol = null

                            try {
                                apiResults = AlcoholApi.getAlcohol(
                                    query,
                                    resultCount = 5
                                )

                                apiResults.forEach { product ->
                                    product.barcode?.let { productId ->
                                        database.alcoholDao().save(
                                            AlcoholEntity(
                                                id = productId,
                                                product_name = product.name
                                                    ?: "Unknown product",
                                                brand = product.brand,
                                                countries = product.countries,
                                                abv = product.abv,
                                                image_url = product.imageUrl
                                            )
                                        )
                                    }
                                }

                                Log.d(
                                    "API_SEARCH",
                                    "Found ${apiResults.size} products for $query"
                                )

                                if (apiResults.isEmpty()) {
                                    errorMessage = "No matching alcohols found."
                                }
                            } catch (error: Exception) {
                                errorMessage =
                                    "Could not load products: ${error.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Search API")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Autocomplete suggestions from Room, limited to five entries.
            if (suggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    suggestions.forEach { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAlcohol = product
                                    searchText = product.product_name
                                    suggestions = emptyList()
                                    apiResults = emptyList()
                                    errorMessage = null
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = product.product_name,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // A selected autocomplete item takes priority and displays alone.
            if (selectedAlcohol != null) {
                val product = selectedAlcohol!!
                LazyColumn(Modifier.padding(top = 24.dp)) {
                    item {
                        Column(Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = product.product_name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            product.brand?.let { Text("Brand: $it") }
                            product.countries?.let { Text("Country: $it") }
                            product.abv?.let { Text("ABV: $it%") }
                            product.image_url?.let { Text("Image: $it") }
                        }
                    }
                }
            } else if (apiResults.isNotEmpty()) {
                // Display results returned by the external API.
                LazyColumn(Modifier.padding(top = 24.dp)) {
                    items(apiResults) { product ->
                        Column(Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = product.name ?: "Unknown product",
                                style = MaterialTheme.typography.titleLarge
                            )
                            product.brand?.let { Text("Brand: $it") }
                            product.category?.let { Text("Category: $it") }
                            product.countries?.let { Text("Country: $it") }
                            product.size?.let { Text("Size: $it") }
                            product.abv?.let { Text("ABV: $it%") }
                            product.barcode?.let { Text("Barcode/SKU: $it") }
                        }
                    }
                }
            } else if (searchText.isNotBlank() && suggestions.isEmpty() && !isLoading) {
                Text(
                    text = "No matching alcohols found.",
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
