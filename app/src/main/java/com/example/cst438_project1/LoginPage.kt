package com.example.cst438_project1

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.ui.theme.ui.theme.Cst438project1Theme

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cst438project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    //variables for user data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Log In")

        Spacer(modifier = Modifier.height(16.dp))
        //Username text box
        OutlinedTextField(
            value = username,
            onValueChange = {username = it},
            label = {Text("Username")}
        )
        //Password text box
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {Text("password")},
            visualTransformation = PasswordVisualTransformation() //hides entered password
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // TODO: Add logic here to check the username and password
        }) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            //TODO: Add logic to open Sign Up Page
        }) {
            Text("Don't have an Account? Sign Up Here")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Cst438project1Theme {
        LoginScreen()
    }
}