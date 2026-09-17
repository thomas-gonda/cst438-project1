package com.example.cst438_project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.ui.theme.Cst438project1Theme

class LandingPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getIntExtra("USER_ID", -1)
        val firstName = intent.getStringExtra("FIRST_NAME") ?: "User"
        val lastName = intent.getStringExtra("LAST_NAME") ?: ""
        val fullName = "$firstName $lastName".trim()

        setContent {
            Cst438project1Theme {
                var showTimeline by rememberSaveable { mutableStateOf(false) }

                if (showTimeline && userId > 0) {
                    TimelinePage(
                        userId = userId,
                        onBack = { showTimeline = false }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        LandingContent(
                            name = fullName,
                            timelineEnabled = userId > 0,
                            onOpenTimeline = { showTimeline = true },
                            onOpenAlcoholSearch = {
                                val searchIntent = Intent(
                                    this,
                                    MainActivity::class.java
                                ).apply {
                                    putExtra("USER_ID", userId)
                                    putExtra("FIRST_NAME", firstName)
                                    putExtra("LAST_NAME", lastName)
                                }
                                startActivity(searchIntent)
                            },
                            onLogout = {
                                startActivity(Intent(this, LoginPage::class.java))
                                finish()
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LandingContent(
    name: String,
    timelineEnabled: Boolean,
    onOpenTimeline: () -> Unit,
    onOpenAlcoholSearch: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome $name!")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "It's Tipsy Time!")
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onOpenAlcoholSearch) {
            Text("Search Alcohols")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onOpenTimeline,
            enabled = timelineEnabled
        ) {
            Text("View Timeline")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onLogout) {
            Text("Log Out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingContentPreview() {
    Cst438project1Theme {
        LandingContent(
            name = "Test User",
            timelineEnabled = true,
            onOpenTimeline = {},
            onOpenAlcoholSearch = {},
            onLogout = {}
        )
    }
}
