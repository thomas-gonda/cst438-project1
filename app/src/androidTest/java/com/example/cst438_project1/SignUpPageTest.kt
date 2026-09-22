package com.example.cst438_project1

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpPageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SignUpPage>()

    @Test
    fun testSignUpFormFillsAndSubmits() {
        // Assert that the title is present
        composeTestRule.onNodeWithText("Create New Account").assertExists()

        // Fill out the sign up form
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("First Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")

        // Click the sign up button
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // You can add further logic to check if a navigation occurred or if a toast was shown if your testing framework supports it.
        // E.g., we could verify that it transitions away or displays certain elements from LandingPage
        // but testing intents and toasts reliably in compose UI tests might need additional setup like intended() from espresso-intents.
    }

    @Test
    fun testSignUpPasswordsDoNotMatch() {
        // Fill out the sign up form with mismatching passwords
        composeTestRule.onNodeWithText("Username").performTextInput("testuser2")
        composeTestRule.onNodeWithText("First Name").performTextInput("Test2")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User2")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password456")

        // Click the sign up button
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // As a simple validation, we remain on this page when it fails so the button should still be present.
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun testSignUpMissingUsername() {
        composeTestRule.onNodeWithText("First Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Should remain on the sign-up page due to missing username
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun testSignUpMissingFirstName() {
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Should remain on the sign-up page due to missing first name
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun testSignUpMissingLastName() {
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("First Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Should remain on the sign-up page due to missing last name
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun testSignUpMissingPassword() {
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("First Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Should remain on the sign-up page due to missing password
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun testSignUpMissingConfirmPassword() {
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("First Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Last Name").performTextInput("User")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Should remain on the sign-up page due to missing confirm password (passwords won't match)
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }
}
