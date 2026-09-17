package com.example.cst438_project1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule // This import changed!
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    // 1. We now explicitly launch your actual LoginPage Activity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginPage>()

    @Test
    fun loginScreen_verifyUIElementsAreDisplayed() {
        // We removed setContent! The rule automatically launched LoginPage,
        // so the UI is already on the screen ready to be tested.

        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an Account? Sign Up Here").assertIsDisplayed()
    }

    @Test
    fun loginScreen_userCanInputCredentialsAndClickSubmit() {
        // Again, no setContent needed here!

        // Action: Simulate a user typing in the Username text box
        composeTestRule.onNodeWithText("Username").performTextInput("alex.rivera")

        // Action: Simulate a user typing in the Password text box
        composeTestRule.onNodeWithText("password").performTextInput("DemoPass123!")

        // Assert: Verify the username text was entered correctly and is displayed
        composeTestRule.onNodeWithText("alex.rivera").assertIsDisplayed()

        // Action: Simulate clicking the Submit button
        composeTestRule.onNodeWithText("Submit").performClick()
    }
}