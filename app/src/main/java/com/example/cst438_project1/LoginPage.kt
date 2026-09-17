package com.example.cst438_project1

import android.content.Context
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
import org.json.JSONArray
import java.io.IOException

//data class for user
data class User(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

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
fun loadUsersFromAssets(context: Context): List<User> {
    val jsonString: String
    try {
        // Open the file and read it into a String
        jsonString = context.assets.open("user_seed.json").bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList() // Return an empty list if there's an error
    }

    val userList = mutableListOf<User>()
    // Parse the String into a JSON Array
    val jsonArray = JSONArray(jsonString)

    // Loop through the array and convert each JSON object into our Kotlin User data class
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        userList.add(
            User(
                username = jsonObject.getString("username"),
                password = jsonObject.getString("password"),
                firstName = jsonObject.getString("first_name"), // Added from JSON schema
                lastName = jsonObject.getString("last_name")    // Added from JSON schema
            )
        )
    }
    return userList
}
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    //variables for user data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    //get the current context
    val context = LocalContext.current
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
            val users = loadUsersFromAssets(context)

            // Checks to see if it is valid
            val validUser = users.find { it.username == username && it.password == password }

            if (validUser != null) {
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                // create an Intent to open the LandingPage
                val intent = android.content.Intent(context, LandingPage::class.java).apply {
                    // attach the user's data to the Intent
                    putExtra("FIRST_NAME", validUser.firstName)
                    putExtra("LAST_NAME", validUser.lastName)
                }
                // Launch the next screen
                context.startActivity(intent)

            } else {
                Toast.makeText(context, "Invalid username or password.", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            //TODO: Add logic to open Sign Up Page
            Toast.makeText(context, "Sign Up clicked!", Toast.LENGTH_SHORT).show()
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
