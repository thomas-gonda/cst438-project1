package com.example.cst438_project1

import android.database.sqlite.SQLiteException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.data.local.AlcoholTimelineEntry
import com.example.cst438_project1.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.TextButton
import android.util.Log
private const val TIMELINE_TAG = "TimelinePage"
@Composable
fun TimelinePage(
    userId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = remember(context) {
        AppDatabase.getInstance(context)
    }

    var entries by remember(userId) {
        mutableStateOf<List<AlcoholTimelineEntry>>(emptyList())
    }
    var isLoading by remember(userId) {
        mutableStateOf(true)
    }
    var errorMessage by remember(userId) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(userId) {
        isLoading = true
        errorMessage = null

        try {
            entries = withContext(Dispatchers.IO) {
                database.alcoholRecordDao()
                    .getTimelineEntries(userId)
            }
        } catch (error: SQLiteException) {
            Log.e(
                TIMELINE_TAG,
                "Could not load timeline for user $userId",
                error
            )
            errorMessage = "Could not load the timeline."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        Text(
            text = "Alcohol Consumption Timeline",
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            isLoading -> TimelineLoading()
            errorMessage != null -> TimelineError(
                message = errorMessage.orEmpty()
            )
            entries.isEmpty() -> EmptyTimeline()
            else -> TimelineList(entries)
        }
    }
}

@Composable
private fun TimelineLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TimelineError(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 24.dp)
    )
}

@Composable
private fun EmptyTimeline() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No alcohol consumption has been recorded yet.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TimelineList(
    entries: List<AlcoholTimelineEntry>
) {
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = entries,
            key = { entry -> entry.recordId }
        ) { entry ->
            TimelineEntryCard(entry)
        }
    }
}

@Composable
private fun TimelineEntryCard(
    entry: AlcoholTimelineEntry
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = entry.productName,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Consumed on: ${entry.date}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = "Rating: ${entry.rating}/5",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )

            entry.userReview?.let { review ->
                Text(
                    text = "Review: $review",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            entry.brand?.let { brand ->
                Text(
                    text = "Brand: $brand",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            entry.abv?.let { abv ->
                Text(
                    text = "ABV: $abv%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
