package easter.egg.passmark

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.compose.ui.text.input.KeyboardType
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
import easter.egg.passmark.utils.accessibility.Describable
import easter.egg.passmark.utils.accessibility.auth.LoginDescribable
import easter.egg.passmark.utils.accessibility.auth.MasterKeyDescribable
import easter.egg.passmark.utils.accessibility.main.AutoLockDescribable
import easter.egg.passmark.utils.accessibility.main.ChangePasswordDescribable
import easter.egg.passmark.utils.accessibility.main.HomeDescribable
import easter.egg.passmark.utils.accessibility.main.PasswordEditDescribable
import easter.egg.passmark.utils.accessibility.main.PasswordViewDescribable
import easter.egg.passmark.utils.accessibility.main.SettingsDescribable
import easter.egg.passmark.utils.testing.PassMarkConfig
import kotlinx.coroutines.Dispatchers
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
    private val TAG = this::class.simpleName

    enum class CustomDelay(private val delay: Long) {
        APP_LAUNCH(delay = 3_000L),

        /** used for small animations which might not require waiting. eg - switching from one text
         * field to another */
        MICRO_ANIMATION(delay = 300L),

        /** for dialogs, bottom sheets, etc */
        SMALL_ANIMATION(delay = 1_200L),
        NAVIGATION(delay = 2_500L),
        GOOGLE_ACCOUNT_SELECT(delay = 1_400L),
        SINGLE_API_CALL(delay = 3_000L + PassMarkConfig.TIME_OUT),
        AUTH_LOADING(delay = 3_000 + (2 * PassMarkConfig.TIME_OUT)),
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

    private val flowName: String? = null // "Timed"

    @Before
    fun startRecording() {
        assert(PassMarkConfig.AutoLockConfig.IS_ENABLED)
        assert(PassMarkConfig.getKeyboardTypeForPasswords() != KeyboardType.Password)
        assert(!PassMarkConfig.USE_SECURE_ACTIVITY)


        if (flowName != null) {
            InstrumentationRegistry.getInstrumentation().uiAutomation.let {
                it.executeShellCommand("mkdir -p /sdcard/TestRecordings")
                it.executeShellCommand("screenrecord /sdcard/TestRecordings/${flowName}Flow.mp4")
            }
        }
    }

    @After
    fun stopRecording() {
        if (flowName != null) {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pkill -l2 screenrecord")
        }
    }

    //---------------------------------------------------------------------------------------utility
    private fun findObject(describable: Describable): UiObject2 {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.desc(describable.desc)), 400)
        return device.findObject(By.desc(describable.desc))
    }

    private fun hasObjectByText(text: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.text(text)), 500)
    }

    private fun type(text: String) {
        InstrumentationRegistry.getInstrumentation().sendStringSync(text)
    }

    private fun type(
        describable: Describable,
        text: String
    ) {
        findObject(describable = describable).click()
        CustomDelay.MICRO_ANIMATION.hold()
        type(text = text)
    }

    private fun clearText() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressKeyCode(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
        CustomDelay.MICRO_ANIMATION.hold()
        device.pressDelete()
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
        findObject(describable = LoginDescribable.GOOGLE_LOGIN_BUTTON).click()
        CustomDelay.GOOGLE_ACCOUNT_SELECT.hold()
        device.findObject(By.text("vaishnav.kanhira@gmail.com")).click()
        CustomDelay.AUTH_LOADING.hold()
    }

    private fun enterMasterKey(masterPassword: String) {
        findObject(describable = MasterKeyDescribable.VISIBILITY).click()
        CustomDelay.MICRO_ANIMATION.hold()
        type(
            describable = MasterKeyDescribable.MASTER_KEY_TEXT_FIELD,
            text = masterPassword
        )
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(describable = MasterKeyDescribable.CONFIRM_BUTTON).click()
        CustomDelay.AUTH_LOADING.hold()
    }

    //-------------------------------------------------------------------passwords-&-vaults-creation
    /** call with an open home drawer and completes with an open drawer (is repeatable) */
    private fun createVault(testVault: TestVault) {
        findObject(describable = HomeDescribable.Drawer.CREATE_NEW_VAULT_BUTTON).click()
        CustomDelay.SMALL_ANIMATION.hold()
        type(
            describable = HomeDescribable.Drawer.VaultDialog.TEXT_FIELD,
            text = testVault.name
        )
        findObject(describable = HomeDescribable.Drawer.VaultDialog.getIconDescribable(index = testVault.iconIndex)).click()
        findObject(describable = HomeDescribable.Drawer.VaultDialog.CREATE_BUTTON).click()
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
        findObject(describable = HomeDescribable.Drawer.VaultDialog.TEXT_FIELD).click()
        CustomDelay.MICRO_ANIMATION.hold()
        clearText()
        type(text = newVaultName)
        device.pressBack()
        findObject(describable = HomeDescribable.Drawer.VaultDialog.UPDATE_BUTTON).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun deleteVault(name: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        hasObjectByText(text = name)
        device.findObject(By.text(name)).longClick()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(describable = HomeDescribable.Drawer.VaultDialog.DELETE_VAULT_BUTTON).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    /** called from home screen, exits to home screen (is repeatable) */
    private fun createPassword(testPasswordData: TestPasswordData) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(
            Until.hasObject(
                By.desc(HomeDescribable.CREATE_NEW_PASSWORD.desc)
            ), 2_000
        )
        findObject(describable = HomeDescribable.CREATE_NEW_PASSWORD).click()
        CustomDelay.NAVIGATION.hold()

        testPasswordData.vault?.let {
            device.wait(
                Until.hasObject(By.desc(PasswordEditDescribable.SELECT_VAULT_BUTTON.desc)),
                2_000
            )
            findObject(describable = PasswordEditDescribable.SELECT_VAULT_BUTTON).click()

            CustomDelay.SMALL_ANIMATION.hold()
            device.findObject(By.text(it)).click()
            CustomDelay.SMALL_ANIMATION.hold()
        }

        fun textHandler(
            describable: Describable,
            text: String?
        ) {
            text?.let { type(describable = describable, text = it) }
        }
        textHandler(
            describable = PasswordEditDescribable.TITLE_TEXT_FIELD,
            text = testPasswordData.title
        )
        textHandler(
            describable = PasswordEditDescribable.EMAIL_TEXT_FIELD,
            text = testPasswordData.email
        )
        textHandler(
            describable = PasswordEditDescribable.USER_NAME_TEXT_FIELD,
            text = testPasswordData.userName
        )
        textHandler(
            describable = PasswordEditDescribable.PASSWORD_TEXT_FIELD,
            text = testPasswordData.password
        )
        textHandler(
            describable = PasswordEditDescribable.WEBSITE_TEXT_FIELD,
            text = testPasswordData.website
        )
        textHandler(
            describable = PasswordEditDescribable.NOTES_TEXT_FIELD,
            text = testPasswordData.note
        )
        device.pressBack()

        if (testPasswordData.useFingerprint) {
            findObject(describable = PasswordEditDescribable.USE_FINGERPRINT_SWITCH).click()
        }
        if (testPasswordData.useLocalStorage) {
            findObject(describable = PasswordEditDescribable.KEEP_LOCAL_SWITCH).click()
        }

        findObject(describable = PasswordEditDescribable.SAVE_BUTTON).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun updatePassword(
        passwordTitleToUpdate: String,
        newPassword: String
    ) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        findObject(describable = HomeDescribable.getPasswordOptionsDescribable(name = passwordTitleToUpdate)).click()
        CustomDelay.SMALL_ANIMATION.hold()

        device.swipe(1080, 2700, 240, 2700, 50)
        findObject(describable = HomeDescribable.PasswordOptionsBottomSheet.EDIT_PASSWORD).click()
        CustomDelay.FINGERPRINT.hold()

        findObject(describable = PasswordEditDescribable.PASSWORD_TEXT_FIELD).click()
        CustomDelay.MICRO_ANIMATION.hold()

        clearText()

        CustomDelay.MICRO_ANIMATION.hold()
        type(newPassword)
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(describable = PasswordEditDescribable.SAVE_BUTTON).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    private fun viewAndDeletePassword(passwordName: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(describable = HomeDescribable.getPasswordDescribable(name = passwordName)).click()
        CustomDelay.NAVIGATION.hold()

        findObject(describable = PasswordViewDescribable.PASSWORD_HISTORY_BUTTON).click()
        CustomDelay.FINGERPRINT.hold()

        device.click(630, 2580)
        CustomDelay.SMALL_ANIMATION.hold()

        UiScrollable(UiSelector().scrollable(true)).scrollToEnd(1)
        device.wait(
            Until.hasObject(By.desc(PasswordViewDescribable.DELETE_PASSWORD_BUTTON.desc)),
            3_000
        )
        findObject(describable = PasswordViewDescribable.DELETE_PASSWORD_BUTTON).click()

        CustomDelay.SMALL_ANIMATION.hold()
        findObject(describable = PasswordViewDescribable.DeletePasswordDialog.DELETE_BUTTON).click()
        CustomDelay.SINGLE_API_CALL.hold()
    }

    //------------------------------------------------------------------------------------------home
    /** to be called from home */
    private fun drawerFunctionality(toOpen: Boolean) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (toOpen) {
            findObject(describable = HomeDescribable.TopBar.OPEN_DRAWER_BUTTON).click()
        } else {
            device.click(1080, 1440)
        }
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun sortPasswordList() {
        findObject(describable = HomeDescribable.TopBar.SORTING).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(
            describable = HomeDescribable.TopBar.Sorting.getSortOptionDescribable(
                passwordSortingOptions = PasswordSortingOptions.NAME
            )
        ).click()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun search() {
        findObject(describable = HomeDescribable.TopBar.SEARCH_BUTTON).click()
        CustomDelay.MICRO_ANIMATION.hold()
        "goo".forEach {
            type(text = it.toString())
            CustomDelay.MICRO_ANIMATION.hold()
        }

        CustomDelay.SMALL_ANIMATION.hold()

        findObject(describable = HomeDescribable.TopBar.BACK_BUTTON).click()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun filterUsingVault() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        device.wait(Until.hasObject(By.text(TestVault.WORK_VAULT)), 1_000)
        device.findObject(By.text(TestVault.WORK_VAULT)).click()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    //-------------------------------------------------------------------------------setting-options
    /** call from home screen with closed drawer */
    private fun changeToNewPassword() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        findObject(describable = HomeDescribable.Drawer.SETTINGS).click()
        CustomDelay.NAVIGATION.hold()
        findObject(describable = SettingsDescribable.CHANGE_PASSWORD_BUTTON).click()
        CustomDelay.NAVIGATION.hold()

        device.wait(
            Until.hasObject(By.desc(ChangePasswordDescribable.ENTER_CURRENT_PASSWORD.desc)),
            3_000
        )

        type(
            describable = ChangePasswordDescribable.ENTER_CURRENT_PASSWORD,
            text = MasterPasswords.OLD_PASSWORD
        )
        type(
            describable = ChangePasswordDescribable.ENTER_NEW_PASSWORD,
            text = MasterPasswords.NEW_PASSWORD
        )
        type(
            describable = ChangePasswordDescribable.REPEAT_NEW_PASSWORD,
            text = MasterPasswords.NEW_PASSWORD
        )
        device.pressBack()
        CustomDelay.MICRO_ANIMATION.hold()
        findObject(describable = ChangePasswordDescribable.CONFIRM).click()
        CustomDelay.CHANGE_PASSWORD.hold()
    }

    /** call from home screen with closed drawer */
    private fun resetUser() {
        drawerFunctionality(toOpen = true)
        findObject(describable = HomeDescribable.Drawer.SETTINGS).click()
        CustomDelay.NAVIGATION.hold()
        findObject(describable = SettingsDescribable.RESET_ACCOUNT_BUTTON).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(describable = SettingsDescribable.ResetUserAccountDialog.RESET_USER_BUTTON).click()
        CustomDelay.RESET_USER.hold()
    }

    /** call from home screen with closed drawer */
    private fun turnOnSwitchesAndLogout() {
        drawerFunctionality(toOpen = true)
        findObject(describable = HomeDescribable.Drawer.SETTINGS).click()
        CustomDelay.NAVIGATION.hold()
        findObject(describable = SettingsDescribable.FINGERPRINT_AUTHENTICATION_SWITCH).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(describable = SettingsDescribable.LOCAL_STORAGE_SWITCH).click()
        CustomDelay.SMALL_ANIMATION.hold()
        findObject(describable = SettingsDescribable.LOG_OUT_BUTTON).click()
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
            findObject(describable = AutoLockDescribable.FINGERPRINT_BUTTON).click()
            CustomDelay.FINGERPRINT.hold()
        } else {
            findObject(describable = AutoLockDescribable.VISIBILITY_BUTTON).click()
            CustomDelay.MICRO_ANIMATION.hold()
            type(
                describable = AutoLockDescribable.PASSWORD_TEXT_FIELD,
                text = passwordToUse
            )
            findObject(describable = AutoLockDescribable.CONFIRM_BUTTON).click()
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

    @Test
    fun timedScript() {
        runBlocking(
            context = Dispatchers.Default,
            block = {
                performTask(
                    taskName = "launch app and login to home",
                    action = {
                        launchApp()
                        selectGoogleAccount()
                        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)
                    }
                )
//                val vaultNameToReplace = "Game"
//                performTask(
//                    taskName = "Create vault",
//                    action = {
//                        drawerFunctionality(toOpen = true)
//                        createVault(testVault = TestingObjects.testVault.copy(name = vaultNameToReplace))
//                    }
//                )
//                performTask(
//                    taskName = "Update vault",
//                    action = {
//                        updateVault(
//                            oldVaultName = vaultNameToReplace,
//                            newVaultName = TestingObjects.testVault.name
//                        )
//                    }
//                )
                performTask(
                    taskName = "Create password",
                    action = {
                        drawerFunctionality(toOpen = false)
                        createPassword(testPasswordData = TestingObjects.testPasswordData)
                    }
                )
                performTask(
                    taskName = "Update password",
                    action = {
                        updatePassword(
                            passwordTitleToUpdate = TestingObjects.testPasswordData.title,
                            newPassword = TestingObjects.getTestingPassword(index = 1)
                        )
                    }
                )
                performTask(
                    taskName = "View and delete password",
                    action = { viewAndDeletePassword(passwordName = TestingObjects.testPasswordData.title) }
                )
                performTask(
                    taskName = "Delete vault",
                    action = {
                        drawerFunctionality(toOpen = true)
                        deleteVault(name = TestingObjects.testVault.name)
                        drawerFunctionality(toOpen = false)
                    }
                )
                performTask(
                    taskName = "Sort, search and filter",
                    action = {
                        sortPasswordList()
                        search()
                        filterUsingVault()
                    }
                )
                performTask(
                    taskName = "Auto-lock and unlock app",
                    action = {
                        lockApp()
                        unlockApp(passwordToUse = MasterPasswords.OLD_PASSWORD)
                    }
                )
                performTask(
                    taskName = "turn on switches and logout",
                    action = {
                        turnOnSwitchesAndLogout()
                    }
                )
                performTask(
                    taskName = "select account and login",
                    action = {
                        selectGoogleAccount()
                        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)
                    }
                )
                performTask(
                    taskName = "Change to new password",
                    action = { changeToNewPassword() }
                )
                performTask(
                    taskName = "Enter master key",
                    action = { enterMasterKey(masterPassword = MasterPasswords.NEW_PASSWORD) }
                )
                performTask(
                    taskName = "Reset user",
                    action = { resetUser() }
                )
            }
        )
    }

    private suspend fun performTask(
        taskName: String,
        action: () -> Unit
    ) = withContext(Dispatchers.IO) {
        fun Long.millisPadded(): String {
            return "${this.toString().padStart(length = 6, padChar = ' ')} ms"
        }

        val startTime = System.currentTimeMillis()
        action()
        delay(timeMillis = 1_000) // spacer between tasks
        val totalTime = System.currentTimeMillis() - startTime
        ("Lap time for " +
                taskName.padEnd(length = 40, padChar = '-') +
                " | Actual = ${totalTime.millisPadded()}").let { msg ->
            Log.d(TAG, msg)
        }
    }
}