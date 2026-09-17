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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.ui.theme.Cst438project1Theme

class LandingPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // retrieve the strings from the Intent
        val firstName = intent.getStringExtra("FIRST_NAME") ?: "User"
        val lastName = intent.getStringExtra("LAST_NAME") ?: ""
        val fullName = "$firstName $lastName".trim()

        setContent {
            Cst438project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
    // grab the current context so we can navigate
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome $name!"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "It's Tipsy Time!"
        )


        Spacer(modifier = Modifier.height(32.dp))

        //Log Out Button
        Button(onClick = {
            // create an Intent to navigate back to LoginPage
            val intent = Intent(context, LoginPage::class.java)
            context.startActivity(intent)

            // finish this Activity so the user cannot use the "Back" button to return here
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Log Out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Cst438project1Theme {
        Greeting2("Test User")
    }
}