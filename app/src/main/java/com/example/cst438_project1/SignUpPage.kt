package com.example.cst438_project1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.data.local.AppDatabase
import com.example.cst438_project1.data.local.UserEntity
import com.example.cst438_project1.ui.theme.Cst438project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cst438project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    SignUP(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun SignUP(modifier: Modifier = Modifier) {
    // Variables for user data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create New Account")

        Spacer(modifier = Modifier.height(16.dp))
        // Username text box
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        //first name text box
        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") }
        )
        //last name text box
        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") }
        )

        // Password text box
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation() // Hides entered password
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation() // Hides entered password
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            //makes sure nothing is left blank
            if (username.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@Button
            }
            //makes sure the passwords match
            if (password != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@Button
            }
            
            coroutineScope.launch {
                val db = AppDatabase.getInstance(context)
                val userDao = db.userDao()
                
                val existingUser = userDao.findByUsername(username)
                //makes sure the username isn't taken
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                
                val newUser = UserEntity(
                    username = username,
                    password = password,
                    first_name = firstname,
                    last_name = lastname
                )
                
                val userId = userDao.insert(newUser).toInt()
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(context, LandingPage::class.java).apply {
                        putExtra("USER_ID", userId)
                        putExtra("FIRST_NAME", firstname)
                        putExtra("LAST_NAME", lastname)
                    }
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                }
            }
        }) {Text("Sign Up")}
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    Cst438project1Theme {
        SignUP()
    }
}