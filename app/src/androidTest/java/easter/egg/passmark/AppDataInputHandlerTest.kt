package easter.egg.passmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import easter.egg.passmark.data.TestPasswordData
import easter.egg.passmark.data.TestVault
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.utils.testing.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDataInputHandlerTest {

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
    private fun createAndSaveNewPassword(password: TestPasswordData) {
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.DISMISS.name))
        password.vault?.let {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name)
                .performClick()
            composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.SELECT_VAULT_DIALOG_CHOOSE_VAULT.name))
            composeRule.onNodeWithText(text = it).performClick()
            composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.EditPassword.DISMISS.name))
        }

        fun type(testTag: String, text: String?) {
            text?.let { composeRule.onNodeWithTag(testTag = testTag).performTextInput(text = it) }
        }

        type(testTag = TestTags.EditPassword.TITLE_TEXT_FIELD.name, text = password.title)
        type(testTag = TestTags.EditPassword.EMAIL_TEXT_FIELD.name, text = password.email)
        type(testTag = TestTags.EditPassword.USER_NAME_TEXT_FIELD.name, text = password.userName)
        type(testTag = TestTags.EditPassword.PASSWORD_TEXT_FIELD.name, text = password.password)
        type(testTag = TestTags.EditPassword.WEBSITE_TEXT_FIELD.name, text = password.website)
        type(testTag = TestTags.EditPassword.NOTES_TEXT_FIELD.name, text = password.note)

        if (password.useFingerprint) {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.USE_FINGERPRINT_SWITCH.name)
                .performClick()
        }
        if (password.useLocalStorage) {
            composeRule
                .onNodeWithTag(testTag = TestTags.EditPassword.KEEP_LOCAL_SWITCH.name)
                .performClick()
        }

        Thread.sleep(8_000)
        composeRule.onNodeWithTag(testTag = TestTags.EditPassword.SAVE_BUTTON.name).performClick()
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name),
            timeoutMillis = 5_000
        )
    }

    /** to be called with the navigation drawer open */
    @OptIn(ExperimentalTestApi::class)
    private fun createVault(
        testVault: TestVault
    ) {
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name))
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name)
            .performTextInput(text = testVault.name)
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.getIconTag(index = testVault.iconIndex))
            .performClick()
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name)
            .performClick()
        composeRule.waitUntilDoesNotExist(
            matcher = hasTestTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name),
            timeoutMillis = 5_000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loginAndSavePasswordsAndVaults() {
        this.createANewUser()
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.OPEN_DRAWER_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.Drawer.TOP_TITLE.name))

        TestVault.vaultTestList.forEach(action = this::createVault)

        Thread.sleep(5_000) // TODO: manually dismiss navigation drawer
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name))
        // TODO: check issue

        TestPasswordData.testList.forEach(this::createAndSaveNewPassword)
    }
}