package easter.egg.passmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import easter.egg.passmark.data.TestPasswordData
import easter.egg.passmark.data.TestVault
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.utils.accessibility.Describable
import easter.egg.passmark.utils.accessibility.screens.HomeDescribable
import easter.egg.passmark.utils.accessibility.auth.LoginDescribable
import easter.egg.passmark.utils.accessibility.auth.MasterKeyDescribable
import easter.egg.passmark.utils.accessibility.screens.PasswordEditDescribable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeFastSetupTest {
    // TODO: switch to describable

    @get:Rule
    val composeRule = createAndroidComposeRule<AuthActivity>()

    @OptIn(ExperimentalTestApi::class)
    fun onNodeWithTag(describable: Describable): SemanticsNodeInteraction {
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(testTag = describable.desc),
            timeoutMillis = 20_000
        )
        return composeRule.onNodeWithTag(testTag = describable.desc)
    }

    /** logs in the user and places them in the home screen */
    @OptIn(ExperimentalTestApi::class)
    fun createANewUser() {
        onNodeWithTag(describable = LoginDescribable.GOOGLE_LOGIN_BUTTON).performClick()
        Thread.sleep(3_000)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .findObject(By.text("vaishnav.kanhira@gmail.com"))
            .click()
        composeRule.waitUntil(
            timeoutMillis = 20_000,
            condition = {
                composeRule
                    .onAllNodesWithTag(MasterKeyDescribable.ENTER_MASTER_KEY_TEXT_FIELD.desc)
                    .fetchSemanticsNodes().isNotEmpty() ||
                        composeRule
                            .onAllNodesWithTag(MasterKeyDescribable.CREATE_MASTER_KEY_TEXT_FIELD.desc)
                            .fetchSemanticsNodes().isNotEmpty()
            }
        )

        val isNewUser = when {
            composeRule
                .onAllNodesWithTag(MasterKeyDescribable.CREATE_MASTER_KEY_TEXT_FIELD.desc)
                .fetchSemanticsNodes().isNotEmpty() -> true

            composeRule
                .onAllNodesWithTag(MasterKeyDescribable.ENTER_MASTER_KEY_TEXT_FIELD.desc)
                .fetchSemanticsNodes().isNotEmpty() -> false

            else -> throw IllegalStateException("one of these should have been null")
        }

        onNodeWithTag(
            describable =
                if (isNewUser) MasterKeyDescribable.CREATE_MASTER_KEY_TEXT_FIELD
                else MasterKeyDescribable.ENTER_MASTER_KEY_TEXT_FIELD
        ).performTextInput("123456789")



        onNodeWithTag(
            describable =
                if (isNewUser) MasterKeyDescribable.CREATE_BUTTON
                else MasterKeyDescribable.CONFIRM_BUTTON
        )
            .performClick()

        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(testTag = HomeDescribable.CREATE_NEW_PASSWORD.desc),
            timeoutMillis = 5_000
        )
    }

    /** ensure that this is called while on home screen with the create new password button visible */
    @OptIn(ExperimentalTestApi::class)
    private fun createAndSaveNewPassword(password: TestPasswordData) {
        onNodeWithTag(describable = HomeDescribable.CREATE_NEW_PASSWORD).performClick()
        password.vault?.let {
            onNodeWithTag(describable = PasswordEditDescribable.SELECT_VAULT_BUTTON).performClick()

            composeRule.waitUntilAtLeastOneExists(
                matcher = hasTestTag(testTag = PasswordEditDescribable.SELECT_VAULT_DIALOG_CHOOSE_VAULT_TITLE.desc),
                timeoutMillis = 5_000
            )
            composeRule.onNodeWithText(text = it).performClick()
            composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = PasswordEditDescribable.DISMISS.desc))
        }

        fun type(describable: Describable, text: String?) {
            text?.let {
                onNodeWithTag(describable = describable).performTextInput(text = it)
            }
        }

        type(describable = PasswordEditDescribable.TITLE_TEXT_FIELD, text = password.title)
        type(describable = PasswordEditDescribable.EMAIL_TEXT_FIELD, text = password.email)
        type(describable = PasswordEditDescribable.USER_NAME_TEXT_FIELD, text = password.userName)
        type(describable = PasswordEditDescribable.PASSWORD_TEXT_FIELD, text = password.password)
        type(describable = PasswordEditDescribable.WEBSITE_TEXT_FIELD, text = password.website)
        type(describable = PasswordEditDescribable.NOTES_TEXT_FIELD, text = password.note)

        if (password.useFingerprint) {
            onNodeWithTag(describable = PasswordEditDescribable.USE_FINGERPRINT_SWITCH).performClick()
        }
        if (password.useLocalStorage) {
            onNodeWithTag(describable = PasswordEditDescribable.KEEP_LOCAL_SWITCH).performClick()
        }

        onNodeWithTag(describable = PasswordEditDescribable.SAVE_BUTTON).performClick()

        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(HomeDescribable.CREATE_NEW_PASSWORD.desc),
            timeoutMillis = 5_000
        )
    }

    /** to be called with the navigation drawer open */
    @OptIn(ExperimentalTestApi::class)
    private fun createVault(
        testVault: TestVault
    ) {
        onNodeWithTag(describable = HomeDescribable.Drawer.CREATE_NEW_VAULT_BUTTON)
            .performClick()
        onNodeWithTag(describable = HomeDescribable.Drawer.VaultDialog.TEXT_FIELD)
            .performTextInput(text = testVault.name)
        onNodeWithTag(describable = HomeDescribable.Drawer.VaultDialog.getIconDescribable(index = testVault.iconIndex))
            .performClick()
        onNodeWithTag(describable = HomeDescribable.Drawer.VaultDialog.CREATE_BUTTON)
            .performClick()
        composeRule.waitUntilDoesNotExist(
            matcher = hasTestTag(testTag = HomeDescribable.Drawer.VaultDialog.CREATE_BUTTON.desc),
            timeoutMillis = 5_000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loginAndSavePasswordsAndVaults() {
        this.createANewUser()

        onNodeWithTag(describable = HomeDescribable.TopBar.OPEN_DRAWER_BUTTON).performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = HomeDescribable.Drawer.SETTINGS.desc))
        TestVault.vaultTestList.forEach(action = this::createVault)

        onNodeWithTag(describable = HomeDescribable.CREATE_NEW_PASSWORD).performClick()
        TestPasswordData.testList.forEach(this::createAndSaveNewPassword)
    }
}