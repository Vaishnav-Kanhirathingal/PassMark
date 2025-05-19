package easter.egg.passmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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

    /** logs in the user and places them in the home screen */
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

    /** ensure that this is called while on home screen with the create new password button visible */
    @OptIn(ExperimentalTestApi::class)
    fun createAndSaveNewPassword(
        routine: String?,
        title: String,
        email: String?,
        userName: String?,
        password: String,
        website: String?,
        note: String?,
        useFingerprint: Boolean,
        keepInLocal: Boolean
    ) {
        // TODO: check
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.DISMISS.name))
        routine?.let {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name)
                .performClick()
            composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.SELECT_VAULT_DIALOG_CHOOSE_VAULT.name))
            composeRule.onNodeWithText(text = it).performClick()
            composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.DISMISS.name))
        }

        composeRule
            .onNodeWithTag(testTag = TestTags.EditPassword.TITLE_TEXT_FIELD.name)
            .performTextInput(text = password)


        fun type(testTag: String, text: String) {
            composeRule.onNodeWithTag(testTag = testTag).performTextInput(text = text)
        }

        userName?.let { type(testTag = TestTags.EditPassword.USER_NAME_TEXT_FIELD.name, text = it) }

        type(testTag = TestTags.EditPassword.PASSWORD_TEXT_FIELD.name, text = password)

        website?.let { type(testTag = TestTags.EditPassword.WEBSITE_TEXT_FIELD.name, text = it) }
        note?.let { type(testTag = TestTags.EditPassword.NOTES_TEXT_FIELD.name, text = it) }

        if (useFingerprint) {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.USE_FINGERPRINT_SWITCH.name)
                .performClick()
        }
        if (keepInLocal) {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.KEEP_LOCAL_SWITCH.name)
                .performClick()
        }

        composeRule.onNodeWithTag(testTag = TestTags.EditPassword.SAVE_BUTTON.name).performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name))
    }
}