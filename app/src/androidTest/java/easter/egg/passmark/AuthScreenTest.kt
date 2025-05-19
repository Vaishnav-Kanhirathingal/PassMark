package easter.egg.passmark

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.utils.testing.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {
    object TestRoutines {
        const val SOCIAL_MEDIA_ROUTINE = "Social Media"
        const val FINANCE_ROUTINE = "Finance"
        const val WORK_ROUTINE = "Work"
    }

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
    private fun createAndSaveNewPassword(
        routine: String?,
        title: String,
        email: String?,
        userName: String?,
        password: String,
        website: String?,
        note: String?,
        useFingerprint: Boolean,
//        keepInLocal: Boolean
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
//        if (keepInLocal) {
//            composeRule
//                .onNodeWithTag(testTag = TestTags.EditPassword.KEEP_LOCAL_SWITCH.name)
//                .performClick()
//        }

        Thread.sleep(10_000)
        composeRule.onNodeWithTag(testTag = TestTags.EditPassword.SAVE_BUTTON.name).performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name))
    }

    /** to be called with the navigation drawer open */
    @OptIn(ExperimentalTestApi::class)
    private fun createRoutine(
        name: String,
        iconIndex: Int
    ) {
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name))
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name)
            .performTextInput(text = name)
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.getIconTag(index = iconIndex))
            .performClick()
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(testTag = TestTags.Home.Drawer.TOP_TITLE.name)
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loginAndSavePasswordsAndVaults() {
        val email = "john.doe@gmail.com"
        val workEmail = "john.doe@SomeCompany.co"
        val note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam feugiat " +
                "lorem magna, in auctor urna molestie ut. Donec venenatis tortor in elit " +
                "scelerisque congue venenatis quis ligula. "

        this.createANewUser()
        composeRule
            .onNodeWithTag(testTag = TestTags.Home.OPEN_DRAWER_BUTTON.name)
            .performClick()
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.Drawer.TOP_TITLE.name))
        createRoutine(name = TestRoutines.SOCIAL_MEDIA_ROUTINE, iconIndex = 3)
        createRoutine(name = TestRoutines.FINANCE_ROUTINE, iconIndex = 2)
        createRoutine(name = TestRoutines.WORK_ROUTINE, iconIndex = 4)

        composeRule.onRoot().performTouchInput {
            click(
                position = Offset(x = this.width.toFloat(), y = (this.height.toFloat() / 2))
            )
        }
        composeRule.waitUntilAtLeastOneExists(matcher = hasTestTag(testTag = TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name))
        // TODO: check issue
        this.createAndSaveNewPassword(
            routine = null,
            title = "Google",
            email = email,
            userName = "John D",
            password = "Google123",
            website = "www.google.com",
            note = note,
            useFingerprint = true,
        )
        this.createAndSaveNewPassword(
            routine = TestRoutines.SOCIAL_MEDIA_ROUTINE,
            title = "FaceBook",
            email = email,
            userName = "john_d",
            password = "FaceBook123",
            website = "www.facebook.com",
            note = note,
            useFingerprint = false,
        )
        this.createAndSaveNewPassword(
            routine = TestRoutines.SOCIAL_MEDIA_ROUTINE,
            title = "Instagram",
            email = email,
            userName = "j_doe",
            password = "insta123",
            website = "www.instagram.com",
            note = note,
            useFingerprint = false,
        )
        this.createAndSaveNewPassword(
            routine = TestRoutines.WORK_ROUTINE,
            title = "LinkedIn",
            email = email,
            userName = "John Marksman Doe",
            password = "insta123",
            website = "www.linkedin.com",
            note = note,
            useFingerprint = true,
        )
        this.createAndSaveNewPassword(
            routine = TestRoutines.WORK_ROUTINE,
            title = "Git-Hub",
            email = email,
            userName = "j_d_112",
            password = "git123",
            website = "www.github.com",
            note = note,
            useFingerprint = true,
        )
        this.createAndSaveNewPassword(
            routine = TestRoutines.WORK_ROUTINE,
            title = "Email",
            email = workEmail,
            userName = "Dr. John Doe",
            password = "Work123",
            website = "www.outlook.com",
            note = note,
            useFingerprint = true,
        )
        this.createAndSaveNewPassword(
            routine = null,
            title = "spotify",
            email = email,
            userName = "jd",
            password = "spoty_123",
            website = "www.spotify.com",
            note = note,
            useFingerprint = false,
        )


        this.createAndSaveNewPassword(
            routine = TestRoutines.FINANCE_ROUTINE,
            title = "RBI",
            email = email,
            userName = "Mr. John Doe",
            password = "fin123",
            website = "www.rbi.org",
            note = note,
            useFingerprint = true,
        )
    }
}