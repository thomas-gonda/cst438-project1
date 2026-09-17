package com.example.cst438_project1

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cst438_project1.data.local.AlcoholExperienceEntity
import com.example.cst438_project1.data.local.AlcoholRecordEntity
import com.example.cst438_project1.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DETAILS_TAG = "ALCOHOL_DETAILS"
private const val STAR_SIZE_SP = 36
private const val DATE_PATTERN = "yyyy-MM-dd"
private const val EXPERIENCE_SAVED_MESSAGE = "Experience saved."
private const val RECORD_ADDED_MESSAGE = "Record added for today."

data class AlcoholDetails(
    val barcode: String?,
    val name: String,
    val brand: String?,
    val category: String?,
    val countries: String?,
    val size: String?,
    val abv: String?,
    val imageUrl: String?
)

private class ExperienceState {
    var rating by mutableStateOf(0)
    var reviewText by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)
}

private class RecordState {
    var records by mutableStateOf<List<AlcoholRecordEntity>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isAdding by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)
}

@Composable
fun AlcoholDetailsScreen(
    userId: Int,
    firstName: String,
    lastName: String,
    alcohol: AlcoholDetails,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    val experienceState = remember(userId, alcohol.barcode) { ExperienceState() }
    val recordState = remember(userId, alcohol.barcode) { RecordState() }

    LoadExperienceEffect(
        database = database,
        userId = userId,
        alcoholId = alcohol.barcode,
        state = experienceState
    )
    LoadRecordsEffect(
        database = database,
        userId = userId,
        alcoholId = alcohol.barcode,
        state = recordState
    )

    AlcoholDetailsLayout(
        userId = userId,
        fullName = "$firstName $lastName".trim(),
        alcohol = alcohol,
        experienceState = experienceState,
        recordState = recordState,
        onBack = onBack,
        onSaveExperience = {
            coroutineScope.launch {
                saveExperience(database, userId, alcohol, experienceState)
            }
        },
        onAddRecord = {
            coroutineScope.launch {
                addRecord(database, userId, alcohol, recordState)
            }
        }
    )
}

@Composable
private fun LoadExperienceEffect(
    database: AppDatabase,
    userId: Int,
    alcoholId: String?,
    state: ExperienceState
) {
    LaunchedEffect(userId, alcoholId) {
        if (userId <= 0 || alcoholId.isNullOrBlank()) {
            return@LaunchedEffect
        }

        state.isLoading = true
        state.statusMessage = null
        try {
            val savedExperience = withContext(Dispatchers.IO) {
                database.alcoholExperienceDao().get(userId, alcoholId)
            }
            state.rating = savedExperience?.rating ?: 0
            state.reviewText = savedExperience?.user_review.orEmpty()
        } catch (error: SQLiteException) {
            Log.e(DETAILS_TAG, "Could not load alcohol experience", error)
            state.statusMessage = "Could not load your experience."
        } finally {
            state.isLoading = false
        }
    }
}

@Composable
private fun LoadRecordsEffect(
    database: AppDatabase,
    userId: Int,
    alcoholId: String?,
    state: RecordState
) {
    LaunchedEffect(userId, alcoholId) {
        if (userId <= 0 || alcoholId.isNullOrBlank()) {
            state.records = emptyList()
            return@LaunchedEffect
        }

        state.isLoading = true
        state.statusMessage = null
        try {
            state.records = withContext(Dispatchers.IO) {
                database.alcoholRecordDao().getForAlcohol(userId, alcoholId)
            }
        } catch (error: SQLiteException) {
            Log.e(DETAILS_TAG, "Could not load alcohol records", error)
            state.records = emptyList()
            state.statusMessage = "Could not load previous records."
        } finally {
            state.isLoading = false
        }
    }
}

@Composable
private fun AlcoholDetailsLayout(
    userId: Int,
    fullName: String,
    alcohol: AlcoholDetails,
    experienceState: ExperienceState,
    recordState: RecordState,
    onBack: () -> Unit,
    onSaveExperience: () -> Unit,
    onAddRecord: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onBack) { Text("Back") }
            AlcoholHeader(alcohol)
            AlcoholInformationCard(alcohol)
            ExperienceSection(
                userId = userId,
                alcoholId = alcohol.barcode,
                state = experienceState,
                onSave = onSaveExperience
            )
            PreviousTimesSection(
                userId = userId,
                alcoholId = alcohol.barcode,
                state = recordState,
                onAddRecord = onAddRecord
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Signed in as $fullName",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AlcoholHeader(alcohol: AlcoholDetails) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = alcohol.name,
        style = MaterialTheme.typography.headlineMedium
    )
    val imageUrl = alcohol.imageUrl
    if (!imageUrl.isNullOrBlank()) {
        AlcoholImage(imageUrl = imageUrl, alcoholName = alcohol.name)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun AlcoholInformationCard(alcohol: AlcoholDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Alcohol Information",
                style = MaterialTheme.typography.titleLarge
            )
            alcohol.brand?.let { DetailItem("Brand", it) }
            alcohol.category?.let { DetailItem("Category", it) }
            alcohol.countries?.let { DetailItem("Country", it) }
            alcohol.size?.let { DetailItem("Size", it) }
            alcohol.abv?.let { DetailItem("ABV", "$it%") }
            alcohol.barcode?.let { DetailItem("Barcode/SKU", it) }
        }
    }
}

@Composable
private fun ExperienceSection(
    userId: Int,
    alcoholId: String?,
    state: ExperienceState,
    onSave: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Your Experience", style = MaterialTheme.typography.titleLarge)
    if (state.isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(vertical = 12.dp))
        return
    }

    Text("Rating")
    StarRating(
        rating = state.rating,
        onRatingChanged = {
            state.rating = it
            state.statusMessage = null
        }
    )
    ReviewField(state)
    ExperienceSaveButton(userId, alcoholId, state, onSave)
    ExperienceMessages(userId, alcoholId, state)
}

@Composable
private fun ReviewField(state: ExperienceState) {
    OutlinedTextField(
        value = state.reviewText,
        onValueChange = {
            state.reviewText = it
            state.statusMessage = null
        },
        label = { Text("Review") },
        placeholder = { Text("Write your review...") },
        minLines = 4,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ExperienceSaveButton(
    userId: Int,
    alcoholId: String?,
    state: ExperienceState,
    onSave: () -> Unit
) {
    val canSave = userId > 0 &&
            !alcoholId.isNullOrBlank() &&
            state.rating in AlcoholExperienceEntity.MIN_RATING..AlcoholExperienceEntity.MAX_RATING &&
            !state.isSaving

    Button(onClick = onSave, enabled = canSave) {
        Text(if (state.isSaving) "Saving..." else "Save Experience")
    }
}

@Composable
private fun ExperienceMessages(
    userId: Int,
    alcoholId: String?,
    state: ExperienceState
) {
    if (state.rating == 0) {
        Text(
            text = "Select a rating from 1 to 5 stars before saving.",
            style = MaterialTheme.typography.bodySmall
        )
    }
    if (alcoholId.isNullOrBlank()) {
        ErrorText("This alcohol does not have a barcode, so its rating and review cannot be saved.")
    }
    if (userId <= 0) {
        ErrorText("No valid user ID was provided, so this experience cannot be saved.")
    }
    state.statusMessage?.let { message ->
        Text(
            text = message,
            color = if (message == EXPERIENCE_SAVED_MESSAGE) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun PreviousTimesSection(
    userId: Int,
    alcoholId: String?,
    state: RecordState,
    onAddRecord: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Previous Times", style = MaterialTheme.typography.titleLarge)
    if (state.isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(vertical = 12.dp))
        return
    }

    RecordsList(state.records)
    val canAdd = userId > 0 && !alcoholId.isNullOrBlank() && !state.isAdding
    Button(onClick = onAddRecord, enabled = canAdd) {
        Text(if (state.isAdding) "Adding..." else "Add New Record")
    }
    state.statusMessage?.let { RecordStatusMessage(it) }
}

@Composable
private fun RecordsList(records: List<AlcoholRecordEntity>) {
    if (records.isEmpty()) {
        Text(
            text = "No previous records for this alcohol.",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    records.forEach { record ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = record.date,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun RecordStatusMessage(message: String) {
    Text(
        text = message,
        color = if (message == RECORD_ADDED_MESSAGE) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        },
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

private suspend fun saveExperience(
    database: AppDatabase,
    userId: Int,
    alcohol: AlcoholDetails,
    state: ExperienceState
) {
    val alcoholId = alcohol.barcode ?: return
    state.isSaving = true
    state.statusMessage = null
    try {
        withContext(Dispatchers.IO) {
            database.alcoholExperienceDao().save(
                AlcoholExperienceEntity(
                    user_id = userId,
                    alc_id = alcoholId,
                    rating = state.rating,
                    user_review = state.reviewText.trim().ifBlank { null }
                )
            )
        }
        state.statusMessage = EXPERIENCE_SAVED_MESSAGE
    } catch (error: SQLiteException) {
        Log.e(DETAILS_TAG, "Could not save alcohol experience", error)
        state.statusMessage = "Could not save your experience."
    } finally {
        state.isSaving = false
    }
}

private suspend fun addRecord(
    database: AppDatabase,
    userId: Int,
    alcohol: AlcoholDetails,
    state: RecordState
) {
    val alcoholId = alcohol.barcode ?: return
    state.isAdding = true
    state.statusMessage = null
    try {
        val updatedRecords = withContext(Dispatchers.IO) {
            database.alcoholRecordDao().insert(
                AlcoholRecordEntity(
                    user_id = userId,
                    alc_id = alcoholId,
                    date = currentDate()
                )
            )
            database.alcoholRecordDao().getForAlcohol(userId, alcoholId)
        }
        state.records = updatedRecords
        state.statusMessage = RECORD_ADDED_MESSAGE
    } catch (error: SQLiteException) {
        Log.e(DETAILS_TAG, "Could not add alcohol record", error)
        state.statusMessage = "Could not add the alcohol record."
    } finally {
        state.isAdding = false
    }
}

private fun currentDate(): String {
    return SimpleDateFormat(DATE_PATTERN, Locale.US).format(Date())
}

@Composable
private fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (star in AlcoholExperienceEntity.MIN_RATING..AlcoholExperienceEntity.MAX_RATING) {
            Text(
                text = if (star <= rating) "★" else "☆",
                fontSize = STAR_SIZE_SP.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onRatingChanged(star) }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AlcoholImage(
    imageUrl: String,
    alcoholName: String
) {
    var bitmap by remember(imageUrl) { mutableStateOf<Bitmap?>(null) }
    var loading by remember(imageUrl) { mutableStateOf(true) }
    var loadFailed by remember(imageUrl) { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        loading = true
        loadFailed = false
        bitmap = loadBitmap(imageUrl)
        loading = false
        loadFailed = bitmap == null
    }

    when {
        loading -> ImageLoadingIndicator()
        bitmap != null -> LoadedAlcoholImage(bitmap!!, alcoholName)
        loadFailed -> ErrorText("Image unavailable.")
    }
}

private suspend fun loadBitmap(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            URL(imageUrl).openStream().use(BitmapFactory::decodeStream)
        } catch (error: IOException) {
            Log.e(DETAILS_TAG, "Could not load alcohol image", error)
            null
        }
    }
}

@Composable
private fun ImageLoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
    }
}

@Composable
private fun LoadedAlcoholImage(bitmap: Bitmap, alcoholName: String) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "$alcoholName image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp)
    )
}
