package com.example.cst438_project1
import androidx.compose.runtime.rememberCoroutineScope
import com.example.cst438_project1.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438_project1.ui.theme.ui.theme.Cst438project1Theme

//data class for user


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
    //get the current context
    val context = LocalContext.current
    val database = remember(context) {
        AppDatabase.getInstance(context)
    }
    val coroutineScope = rememberCoroutineScope()
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

        Button(
            onClick = {
                coroutineScope.launch {
                    val savedUser = withContext(Dispatchers.IO) {
                        database.userDao()
                            .findByUsername(username.trim())
                    }

                    withContext(Dispatchers.Main.immediate) {
                        if (
                            savedUser != null &&
                            savedUser.password == password
                        ) {
                            Toast.makeText(
                                context,
                                "Login Successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = android.content.Intent(
                                context,
                                LandingPage::class.java
                            ).apply {
                                putExtra("USER_ID", savedUser.id)
                                putExtra("FIRST_NAME", savedUser.first_name)
                                putExtra("LAST_NAME", savedUser.last_name)
                            }

                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid username or password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            val intent = android.content.Intent(context, SignUpPage::class.java)
            context.startActivity(intent)
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
