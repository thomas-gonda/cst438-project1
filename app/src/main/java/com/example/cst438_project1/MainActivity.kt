package com.example.cst438_project1

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TextButton
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
import java.io.IOException

private const val API_SEARCH_TAG = "API_SEARCH"
private const val AUTOCOMPLETE_RESULT_LIMIT = 5

private data class SearchOutcome(
    val products: List<Alcohol> = emptyList(),
    val errorMessage: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getIntExtra("USER_ID", -1)
        val firstName = intent.getStringExtra("FIRST_NAME") ?: "User"
        val lastName = intent.getStringExtra("LAST_NAME") ?: ""

        setContent {
            Cst438project1Theme {
                MainContent(
                    userId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    userId: Int,
    firstName: String,
    lastName: String,
    onBack: () -> Unit
) {
    var alcoholDetails by remember { mutableStateOf<AlcoholDetails?>(null) }

    BackHandler(enabled = alcoholDetails != null) {
        alcoholDetails = null
    }

    val selectedAlcohol = alcoholDetails
    if (selectedAlcohol == null) {
        AlcoholSearchScreen(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            onBack = onBack,
            onAlcoholClick = { alcoholDetails = it }
        )
    } else {
        AlcoholDetailsScreen(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            alcohol = selectedAlcohol,
            onBack = { alcoholDetails = null }
        )
    }
}

@Composable
fun AlcoholSearchScreen(
    userId: Int,
    firstName: String,
    lastName: String,
    onBack: () -> Unit,
    onAlcoholClick: (AlcoholDetails) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AlcoholEntity>>(emptyList()) }
    var apiResults by remember { mutableStateOf<List<Alcohol>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }

    LaunchedEffect(searchText) {
        suggestions = findSuggestions(searchText, database)
    }

    Scaffold { innerPadding ->
        SearchScreenContent(
            modifier = Modifier.padding(innerPadding),
            userId = userId,
            fullName = "$firstName $lastName".trim(),
            searchText = searchText,
            suggestions = suggestions,
            apiResults = apiResults,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onBack = onBack,
            onSearchTextChange = {
                searchText = it
                apiResults = emptyList()
                errorMessage = null
            },
            onSearch = {
                coroutineScope.launch {
                    isLoading = true
                    val outcome = performSearch(searchText, database)
                    apiResults = outcome.products
                    errorMessage = outcome.errorMessage
                    isLoading = false
                }
            },
            onAlcoholClick = onAlcoholClick
        )
    }
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier,
    userId: Int,
    fullName: String,
    searchText: String,
    suggestions: List<AlcoholEntity>,
    apiResults: List<Alcohol>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onAlcoholClick: (AlcoholDetails) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        SearchHeader(userId, fullName)
        SearchField(searchText, onSearchTextChange)
        SearchApiButton(isLoading, onSearch)
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        SuggestionsList(
            suggestions = suggestions,
            onSuggestionSelected = { onAlcoholClick(it.toAlcoholDetails()) }
        )
        SearchResults(
            apiResults = apiResults,
            searchText = searchText,
            suggestions = suggestions,
            isLoading = isLoading,
            onAlcoholClick = onAlcoholClick
        )
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SearchHeader(userId: Int, fullName: String) {
    Text(
        text = "Alcohol search",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Signed in as $fullName",
        style = MaterialTheme.typography.bodyMedium
    )
    if (userId <= 0) {
        Text(
            text = "No valid user ID was provided.",
            color = MaterialTheme.colorScheme.error
        )
    }
}

private suspend fun performSearch(
    searchText: String,
    database: AppDatabase
): SearchOutcome {
    val query = searchText.trim()
    if (query.isEmpty()) {
        return SearchOutcome(errorMessage = "Enter an alcohol name to search.")
    }

    return try {
        val products = searchAndSaveAlcohol(query, database)
        Log.d(API_SEARCH_TAG, "Found ${products.size} products for $query")
        SearchOutcome(
            products = products,
            errorMessage = if (products.isEmpty()) "No matching alcohols found." else null
        )
    } catch (error: IOException) {
        Log.e(API_SEARCH_TAG, "Network request failed", error)
        SearchOutcome(errorMessage = "Network error. Check your internet connection.")
    } catch (error: SQLiteException) {
        Log.e(API_SEARCH_TAG, "Database save failed", error)
        SearchOutcome(errorMessage = "Could not save the products locally.")
    }
}

private suspend fun findSuggestions(
    searchText: String,
    database: AppDatabase
): List<AlcoholEntity> {
    if (searchText.isBlank()) {
        return emptyList()
    }

    return withContext(Dispatchers.IO) {
        database.alcoholDao()
            .getAll()
            .filter { product ->
                product.product_name.contains(searchText.trim(), ignoreCase = true)
            }
            .sortedBy { it.product_name.lowercase() }
            .take(AUTOCOMPLETE_RESULT_LIMIT)
    }
}

private suspend fun searchAndSaveAlcohol(
    query: String,
    database: AppDatabase
): List<Alcohol> {
    val products = AlcoholApi.getAlcohol(
        query,
        resultCount = AUTOCOMPLETE_RESULT_LIMIT
    )

    products.forEach { product ->
        product.barcode?.let { productId ->
            database.alcoholDao().save(
                AlcoholEntity(
                    id = productId,
                    product_name = product.name ?: "Unknown product",
                    brand = product.brand,
                    countries = product.countries,
                    abv = product.abv,
                    image_url = product.imageUrl
                )
            )
        }
    }
    return products
}

@Composable
private fun SearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        label = { Text("Search alcohols") },
        placeholder = { Text("Type a product name") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
private fun SearchApiButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Text("Search API")
    }
}

@Composable
private fun SuggestionsList(
    suggestions: List<AlcoholEntity>,
    onSuggestionSelected: (AlcoholEntity) -> Unit
) {
    if (suggestions.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        suggestions.forEach { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clickable { onSuggestionSelected(product) }
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

@Composable
private fun SearchResults(
    apiResults: List<Alcohol>,
    searchText: String,
    suggestions: List<AlcoholEntity>,
    isLoading: Boolean,
    onAlcoholClick: (AlcoholDetails) -> Unit
) {
    when {
        apiResults.isNotEmpty() -> ApiResultsList(apiResults, onAlcoholClick)
        searchText.isNotBlank() && suggestions.isEmpty() && !isLoading -> Text(
            text = "No matching alcohols found.",
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
private fun ApiResultsList(
    apiResults: List<Alcohol>,
    onAlcoholClick: (AlcoholDetails) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp)) {
        items(apiResults) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable { onAlcoholClick(product.toAlcoholDetails()) }
            ) {
                AlcoholResultContent(product)
            }
        }
    }
}

@Composable
private fun AlcoholResultContent(product: Alcohol) {
    Column(modifier = Modifier.padding(16.dp)) {
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

private fun Alcohol.toAlcoholDetails(): AlcoholDetails {
    return AlcoholDetails(
        barcode = barcode,
        name = name ?: "Unknown product",
        brand = brand,
        category = category,
        countries = countries,
        size = size,
        abv = abv?.toString(),
        imageUrl = imageUrl
    )
}

private fun AlcoholEntity.toAlcoholDetails(): AlcoholDetails {
    return AlcoholDetails(
        barcode = id,
        name = product_name,
        brand = brand,
        category = null,
        countries = countries,
        size = null,
        abv = abv?.toString(),
        imageUrl = image_url
    )
}
