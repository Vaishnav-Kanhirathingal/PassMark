package easter.egg.passmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.utils.testing.TestTags
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
            matcher = hasTestTag(testTag = TestTags.Login.GOOGLE_BUTTON.name),
            timeoutMillis = 5_000
        )
        composeRule.onNodeWithTag(TestTags.Login.GOOGLE_BUTTON.name).performClick()
        Thread.sleep(6_000)
        // opens create new user screen
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(testTag = TestTags.CreateMasterKey.CONFIRM_BUTTON.name),
            timeoutMillis = 3_000
        )

        composeRule.onNodeWithTag(testTag = TestTags.CreateMasterKey.TEXT_FIELD.name)
            .performTextInput("123456789")
        Thread.sleep(1000)
        composeRule.onNodeWithTag(testTag = TestTags.CreateMasterKey.CONFIRM_BUTTON.name)
            .performClick()

        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(testTag = TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name),
            timeoutMillis = 5_000
        )
    }
}