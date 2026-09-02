package com.example.cst438_project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.example.cst438_project1.data.model.Alcohol
import com.example.cst438_project1.data.remote.AlcoholApi
import com.example.cst438_project1.ui.theme.Cst438project1Theme
import kotlinx.coroutines.launch

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

@androidx.compose.runtime.Composable
fun AlcoholSearchScreen() {
    var alcohol by remember { mutableStateOf<List<Alcohol>>(emptyList()) }
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
            Text("Alcohol search", style = MaterialTheme.typography.headlineMedium)

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            alcohol = AlcoholApi.getAlcohol("vodka", resultCount = 5)
                            if (alcohol.isEmpty()) errorMessage = "No matching products found."
                        } catch (error: Exception) {
                            errorMessage = "Could not load product: ${error.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Find 5 vodkas")
            }

            if (isLoading) {
                CircularProgressIndicator(Modifier.padding(top = 24.dp))
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            LazyColumn(Modifier.padding(top = 16.dp)) {
                items(alcohol) { product ->
                    Column(Modifier.padding(bottom = 16.dp)) {
                        Text(product.name ?: "Unknown product", style = MaterialTheme.typography.titleLarge)
                        product.brand?.let { Text("Brand: $it") }
                        product.category?.let { Text("Category: $it") }
                        product.countries?.let { Text("Country: $it") }
                        product.size?.let { Text("Size: $it") }
                        product.abv?.let { Text("ABV: $it%") }
                        product.barcode?.let { Text("Barcode/SKU: $it") }
                    }
                }
            }
        }
    }
}