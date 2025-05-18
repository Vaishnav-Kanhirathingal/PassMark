package easter.egg.passmark

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
    @Test
    fun createANewUser() {
        Thread.sleep(3_000)
        composeRule.onNodeWithText("Google").performClick()
        Thread.sleep(10_000) // click account, loader loads, pushing the app to create master password screen

    }
}