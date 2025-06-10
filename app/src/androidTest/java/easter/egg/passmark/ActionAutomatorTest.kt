package easter.egg.passmark

import android.content.Intent
import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import easter.egg.passmark.data.TestPasswordData
import easter.egg.passmark.data.TestVault
import easter.egg.passmark.data.models.password.PasswordSortingOptions
import easter.egg.passmark.ui.main.change_password.ChangeMasterPasswordViewModel
import easter.egg.passmark.ui.main.change_password.ReEncryptionStates
import easter.egg.passmark.ui.main.settings.DeletionStages
import easter.egg.passmark.ui.main.settings.SettingsViewModel
import easter.egg.passmark.utils.testing.TestTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.absoluteValue

@RunWith(AndroidJUnit4::class)
class ActionAutomatorTest {
    // TODO: replace all theme home screen images since search changed
    // TODO: bug in edit from view. not updating in view

    enum class CustomDelay(private val delay: Long) {
        APP_LAUNCH(delay = 3_000L),

        /** used for small animations which might not require waiting. eg - switching from one text
         * field to another */
        MICRO_ANIMATION(delay = 300L),

        /** for dialogs, bottom sheets, etc */
        SMALL_ANIMATION(delay = 1_200L),
        NAVIGATION(delay = 2_500L),
        SINGLE_API_CALL(delay = 3_000L + TestTags.TIME_OUT),
        AUTH_LOADING(delay = 3_000 + (2 * TestTags.TIME_OUT)),
        FINGERPRINT(delay = 5_000L),
        CHANGE_PASSWORD(
            delay = 3_000 + ((ChangeMasterPasswordViewModel.LOOP_DELAY + 500) * ReEncryptionStates.entries.size)
        ),
        RESET_USER(
            delay = 3_000 + ((SettingsViewModel.LOOP_DELAY + 500) * DeletionStages.entries.size)
        );

        fun hold() {
            Thread.sleep(this.delay)
        }
    }

    private object MasterPasswords {
        const val OLD_PASSWORD = "123456789"
        const val NEW_PASSWORD = "987654321"
    }

    private object TestingObjects {
        val testVault: TestVault = TestVault(
            name = "Games",
            iconIndex = 11
        )

        fun getTestingPassword(index: Int): String {
            return when ((index % 4).absoluteValue) {
                0 -> "BlackWell"
                1 -> "LoveLace"
                2 -> "Ampere"
                else -> "Turing"
            }
        }

        val testPasswordData = TestPasswordData(
            vault = testVault.name,
            title = "Nvidia",
            email = "johnDoe@nvidia.com",
            userName = "JensenHuang",
            password = getTestingPassword(index = 0),
            website = "nvidia.com",
            note = "3rd gen",
            useFingerprint = true,
            useLocalStorage = true
        )
    }

    //-------------------------------------------------------------------------------------recording

    private val themeName: String = "AppLock"

    @Before
    fun startRecording() {
        val command = "screenrecord /sdcard/TestRecordings/${themeName}Flow.mp4"
        InstrumentationRegistry.getInstrumentation().uiAutomation.let {
            it.executeShellCommand("mkdir -p /sdcard/TestRecordings")
            it.executeShellCommand(command)
        }

    }

    @After
    fun stopRecording() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pkill -l2 screenrecord")
    }

    //---------------------------------------------------------------------------------------utility
    private fun findObject(testTag: String): UiObject2 {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.desc(testTag)), 500)
        return device.findObject(By.desc(testTag))
    }

    private fun hasObjectByText(text: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.text(text)), 500)
    }

    private fun type(text: String) {
        InstrumentationRegistry.getInstrumentation().sendStringSync(text)
    }

    private fun type(
        testTag: String,
        text: String
    ) {
        findObject(testTag = testTag).click()
        CustomDelay.MICRO_ANIMATION.hold()
        type(text = text)
    }

    private fun clearText() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressKeyCode(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
        device.pressDelete()
    }

    private fun UiObject2.visibleClick() {
        this.click(300)
    }

    //-------------------------------------------------------------------------------------start-app
    private fun launchApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val packageName = "easter.egg.passmark"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent!!.apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) })
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000)
        CustomDelay.APP_LAUNCH.hold()
    }

    //------------------------------------------------------------------------------------------auth
    private fun selectGoogleAccount() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(TestTags.Login.GOOGLE_BUTTON.name).click()
        CustomDelay.NAVIGATION.hold()
        device.findObject(By.text("vaishnav.kanhira@gmail.com")).click()
        CustomDelay.AUTH_LOADING.hold()
    }

    private fun enterMasterKey(masterPassword: String) {
        findObject(testTag = TestTags.CreateMasterKey.VISIBILITY_BUTTON.name).click()
        CustomDelay.MICRO_ANIMATION.hold()
        type(
            testTag = TestTags.CreateMasterKey.TEXT_FIELD.name,
            text = masterPassword
        )
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(TestTags.CreateMasterKey.CONFIRM_BUTTON.name).click()
        CustomDelay.AUTH_LOADING.hold()
    }

    //-------------------------------------------------------------------passwords-&-vaults-creation
    /** call with an open home drawer and completes with an open drawer (is repeatable) */
    private fun createVault(testVault: TestVault) {
        findObject(TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
        type(
            testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name,
            text = testVault.name
        )
        findObject(TestTags.Home.Drawer.VaultDialog.getIconTag(index = testVault.iconIndex)).click()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun updateVault(
        @Suppress("SameParameterValue") oldVaultName: String,
        newVaultName: String
    ) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        hasObjectByText(text = oldVaultName)
        device.findObject(By.text(oldVaultName)).longClick()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name).click()
        clearText()
        type(text = newVaultName)
        device.pressBack()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun deleteVault(name: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        hasObjectByText(text = name)
        device.findObject(By.text(name)).longClick()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.DELETE_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    /** called from home screen, exits to home screen (is repeatable) */
    private fun createPassword(testPasswordData: TestPasswordData) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.wait(Until.hasObject(By.desc(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name)), 2_000)
        findObject(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name).click()
        CustomDelay.NAVIGATION.hold()

        testPasswordData.vault?.let {
            device.wait(
                Until.hasObject(By.desc(TestTags.EditPassword.SELECT_VAULT_BUTTON.name)),
                2_000
            )
            this.findObject(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name).click()

            CustomDelay.SMALL_ANIMATION.hold()
            device.findObject(By.text(it)).click()
            CustomDelay.SMALL_ANIMATION.hold()
        }

        fun textHandler(
            testTag: String,
            text: String?
        ) {
            text?.let { type(testTag = testTag, text = it) }
        }
        textHandler(
            testTag = TestTags.EditPassword.TITLE_TEXT_FIELD.name,
            text = testPasswordData.title
        )
        textHandler(
            testTag = TestTags.EditPassword.EMAIL_TEXT_FIELD.name,
            text = testPasswordData.email
        )
        textHandler(
            testTag = TestTags.EditPassword.USER_NAME_TEXT_FIELD.name,
            text = testPasswordData.userName
        )
        textHandler(
            testTag = TestTags.EditPassword.PASSWORD_TEXT_FIELD.name,
            text = testPasswordData.password
        )
        textHandler(
            testTag = TestTags.EditPassword.WEBSITE_TEXT_FIELD.name,
            text = testPasswordData.website
        )
        textHandler(
            testTag = TestTags.EditPassword.NOTES_TEXT_FIELD.name,
            text = testPasswordData.note
        )
        device.pressBack()

        if (testPasswordData.useFingerprint) {
            findObject(testTag = TestTags.EditPassword.USE_FINGERPRINT_SWITCH.name).click()
        }
        if (testPasswordData.useLocalStorage) {
            findObject(testTag = TestTags.EditPassword.KEEP_LOCAL_SWITCH.name).click()
        }

        findObject(testTag = TestTags.EditPassword.SAVE_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun updatePassword(
        passwordTitleToUpdate: String,
        newPassword: String
    ) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        findObject(testTag = TestTags.Home.getPasswordOptionsTag(name = passwordTitleToUpdate)).click()
        CustomDelay.SMALL_ANIMATION.hold()

        device.swipe(1080, 2700, 240, 2700, 50)
        findObject(testTag = TestTags.Home.PasswordOptionsBottomSheet.EDIT_BUTTON.name).click()
        CustomDelay.FINGERPRINT.hold()

        findObject(testTag = TestTags.EditPassword.PASSWORD_TEXT_FIELD.name).click()
        CustomDelay.MICRO_ANIMATION.hold()

        clearText()

        CustomDelay.MICRO_ANIMATION.hold()
        type(newPassword)
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(testTag = TestTags.EditPassword.SAVE_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun viewAndDeletePassword(passwordName: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(testTag = TestTags.Home.getPasswordTag(name = passwordName)).click()
        CustomDelay.NAVIGATION.hold()

        findObject(testTag = TestTags.ViewPassword.PASSWORD_HISTORY_BUTTON.name).click()
        CustomDelay.FINGERPRINT.hold()

        device.click(630, 2580)
        CustomDelay.SMALL_ANIMATION.hold()

        UiScrollable(UiSelector().scrollable(true)).scrollToEnd(1)
        device.wait(Until.hasObject(By.desc(TestTags.ViewPassword.DELETE_BUTTON.name)), 3_000)
        findObject(testTag = TestTags.ViewPassword.DELETE_BUTTON.name).click()

        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.ConfirmationDialog.POSITIVE_BUTTON.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    //------------------------------------------------------------------------------------------home
    /** to be called from home */
    private fun drawerFunctionality(toOpen: Boolean) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (toOpen) {
            findObject(testTag = TestTags.Home.TopBar.OPEN_DRAWER_BUTTON.name).click()
        } else {
            device.click(1080, 1440)
        }
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun sortPasswordList() {
        findObject(testTag = TestTags.Home.TopBar.SORTING_BUTTON.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.Home.Sorting.getSortOptionTag(passwordSortingOptions = PasswordSortingOptions.NAME)).visibleClick()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun search() {
        findObject(testTag = TestTags.Home.TopBar.SEARCH_BUTTON.name).visibleClick()
        CustomDelay.MICRO_ANIMATION.hold()
        "goo".forEach {
            type(text = it.toString())
            CustomDelay.MICRO_ANIMATION.hold()
        }

        CustomDelay.SMALL_ANIMATION.hold()

        findObject(testTag = TestTags.Home.TopBar.BACK_BUTTON.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun filterUsingVault() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        device.wait(Until.hasObject(By.text(TestVault.WORK_VAULT)), 1_000)
        device.findObject(By.text(TestVault.WORK_VAULT)).visibleClick()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    //-------------------------------------------------------------------------------setting-options
    /** call from home screen with closed drawer */
    private fun changeToNewPassword() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        findObject(testTag = TestTags.Home.Drawer.SETTINGS.name).click()
        CustomDelay.NAVIGATION.hold()
        findObject(testTag = TestTags.Settings.CHANGE_PASSWORD_BUTTON.name).click()
        CustomDelay.NAVIGATION.hold()

        device.wait(
            Until.hasObject(By.desc(TestTags.ChangePassword.ORIGINAL_PASSWORD_TEXT_FIELD.name)),
            3_000
        )

        type(
            testTag = TestTags.ChangePassword.ORIGINAL_PASSWORD_TEXT_FIELD.name,
            text = MasterPasswords.OLD_PASSWORD
        )
        type(
            testTag = TestTags.ChangePassword.NEW_PASSWORD_TEXT_FIELD.name,
            text = MasterPasswords.NEW_PASSWORD
        )
        type(
            testTag = TestTags.ChangePassword.NEW_PASSWORD_REPEATED_TEXT_FIELD.name,
            text = MasterPasswords.NEW_PASSWORD
        )
        device.pressBack()
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(testTag = TestTags.ChangePassword.CONFIRM_BUTTON.name).click()
        CustomDelay.CHANGE_PASSWORD.hold()
    }

    /** call from home screen with closed drawer */
    private fun resetUser() {
        drawerFunctionality(toOpen = true)
        findObject(testTag = TestTags.Home.Drawer.SETTINGS.name).click()
        CustomDelay.NAVIGATION.hold()
        findObject(testTag = TestTags.Settings.RESET_ACCOUNT_BUTTON.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.ConfirmationDialog.POSITIVE_BUTTON.name).click()
        CustomDelay.RESET_USER.hold()
    }

    /** call from home screen with closed drawer */
    private fun turnOnSwitchesAndLogout() {
        drawerFunctionality(toOpen = true)
        findObject(testTag = TestTags.Home.Drawer.SETTINGS.name).click()
        CustomDelay.NAVIGATION.hold()
        findObject(testTag = TestTags.Settings.FINGERPRINT_AUTHENTICATION_SWITCH.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.Settings.LOCAL_STORAGE_SWITCH.name).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(testTag = TestTags.Settings.LOG_OUT.name).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    //-------------------------------------------------------------------------------------auto-lock
    private fun lockApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressRecentApps()
        CustomDelay.SINGLE_API_CALL.hold()
        device.click(630, 1440)
        CustomDelay.NAVIGATION.hold()
    }

    /** To be called from app's lock screen
     * @param passwordToUse keep null for fingerprint */
    private fun unlockApp(passwordToUse: String?) {
        if (passwordToUse == null) {
            findObject(testTag = TestTags.AutoLock.FINGERPRINT_BUTTON.name).visibleClick()
            CustomDelay.FINGERPRINT.hold()
        } else {
            findObject(testTag = TestTags.AutoLock.VISIBILITY_BUTTON.name).visibleClick()
            CustomDelay.MICRO_ANIMATION.hold()
            type(
                testTag = TestTags.AutoLock.PASSWORD_TEXT_FIELD.name,
                text = passwordToUse
            )
            findObject(testTag = TestTags.AutoLock.CONFIRM_BUTTON.name).visibleClick()
            CustomDelay.SINGLE_API_CALL.hold()
        }
    }

    //----------------------------------------------------------------------------------final-script
    @Test
    fun fullScript() {
        launchApp()
        selectGoogleAccount()
        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)

        drawerFunctionality(toOpen = true)
        val vaultNameToReplace = "Game"
        createVault(testVault = TestingObjects.testVault.copy(name = vaultNameToReplace))
        updateVault(
            oldVaultName = vaultNameToReplace,
            newVaultName = TestingObjects.testVault.name
        )

        drawerFunctionality(toOpen = false)

        createPassword(testPasswordData = TestingObjects.testPasswordData)

        repeat(
            times = 3,
            action = {
                updatePassword(
                    passwordTitleToUpdate = TestingObjects.testPasswordData.title,
                    newPassword = TestingObjects.getTestingPassword(index = it + 1)
                )
            }
        )

        viewAndDeletePassword(passwordName = TestingObjects.testPasswordData.title)

        sortPasswordList()
        search()
        filterUsingVault()

        changeToNewPassword()
        enterMasterKey(masterPassword = MasterPasswords.NEW_PASSWORD)
        resetUser()
    }
}