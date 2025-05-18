package easter.egg.passmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import easter.egg.passmark.ui.auth.AuthActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<AuthActivity>()

    /** use this test when the app has no data and user does not exist in remote database */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun createANewUser() {
        // login screen
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasText(text = "Google"),
            timeoutMillis = 5_000
        )
        Thread.sleep(3_000)
        composeRule.onNodeWithText("Google").performClick()
        Thread.sleep(8_000)
        // opens create new user screen
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasText("Create"),
            timeoutMillis = 5_000
        )

        composeRule.onNodeWithText(text = "Master Password").performTextInput("123456789")
        composeRule.onNodeWithText(text = "Create").performClick()

        composeRule.waitUntilAtLeastOneExists(
            matcher = hasText(text = "Search Passwords"),
            timeoutMillis = 5_000
        )
    }
}