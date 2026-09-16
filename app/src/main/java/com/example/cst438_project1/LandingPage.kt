package com.example.cst438_project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cst438_project1.ui.theme.Cst438project1Theme

class LandingPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // retrieve the strings from the Intent (default to empty string if not found)
        val firstName = intent.getStringExtra("FIRST_NAME") ?: "User"
        val lastName = intent.getStringExtra("LAST_NAME") ?: ""

        // combine them into a full name
        val fullName = "$firstName $lastName".trim()

        setContent {
            Cst438project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 3. Pass the full name to your composable
                    Greeting2(
                        name = fullName,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome $name!",
            modifier = modifier
        )
        Text(
            text = "It's Tipsy Time! "
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Cst438project1Theme {
        Greeting2("Test User")
    }
}