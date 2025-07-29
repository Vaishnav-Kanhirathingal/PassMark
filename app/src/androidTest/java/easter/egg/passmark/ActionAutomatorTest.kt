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
    // TODO: switch to describable
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
    private fun findObject(testTag: String): UiObject2 {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.desc(testTag)), 400)
        return device.findObject(By.desc(testTag))
    }

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
        testTag: String,
        text: String
    ) {
        findObject(testTag = testTag).click()
        CustomDelay.MICRO_ANIMATION.hold()
        type(text = text)
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
        findObject(describable = MasterKeyDescribable.VISIBILITY_ON).click()
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
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.DELETE_BUTTON.name).click()
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
        findObject(testTag = TestTags.Home.Sorting.getSortOptionTag(passwordSortingOptions = PasswordSortingOptions.NAME)).click()
        CustomDelay.SMALL_ANIMATION.hold()
    }

    private fun search() {
        findObject(testTag = TestTags.Home.TopBar.SEARCH_BUTTON.name).click()
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
        device.findObject(By.text(TestVault.WORK_VAULT)).click()
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
            findObject(testTag = TestTags.AutoLock.FINGERPRINT_BUTTON.name).click()
            CustomDelay.FINGERPRINT.hold()
        } else {
            findObject(testTag = TestTags.AutoLock.VISIBILITY_BUTTON.name).click()
            CustomDelay.MICRO_ANIMATION.hold()
            type(
                testTag = TestTags.AutoLock.PASSWORD_TEXT_FIELD.name,
                text = passwordToUse
            )
            findObject(testTag = TestTags.AutoLock.CONFIRM_BUTTON.name).click()
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
                holdFor(
                    taskName = "launch app and login to home",
                    estimatedTime = 26_000,
                    action = {
                        launchApp()
                        selectGoogleAccount()
                        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)
                    }
                )
                val vaultNameToReplace = "Game"
                holdFor(
                    taskName = "Create vault",
                    estimatedTime = 14_500,
                    action = {
                        drawerFunctionality(toOpen = true)
                        createVault(testVault = TestingObjects.testVault.copy(name = vaultNameToReplace))
                    }
                )
                holdFor(
                    taskName = "Update vault",
                    estimatedTime = 15_000,
                    action = {
                        updateVault(
                            oldVaultName = vaultNameToReplace,
                            newVaultName = TestingObjects.testVault.name
                        )
                    }
                )
                holdFor(
                    taskName = "Create password",
                    estimatedTime = 28_000,
                    action = {
                        drawerFunctionality(toOpen = false)
                        createPassword(testPasswordData = TestingObjects.testPasswordData)
                    }
                )
                holdFor(
                    taskName = "Update password",
                    estimatedTime = 19_000,
                    action = {
                        updatePassword(
                            passwordTitleToUpdate = TestingObjects.testPasswordData.title,
                            newPassword = TestingObjects.getTestingPassword(index = 1)
                        )
                    }
                )
                holdFor(
                    taskName = "View and delete password",
                    estimatedTime = 22_000,
                    action = { viewAndDeletePassword(passwordName = TestingObjects.testPasswordData.title) }
                )
                holdFor(
                    taskName = "Delete vault",
                    estimatedTime = 14_000,
                    action = {
                        drawerFunctionality(toOpen = true)
                        deleteVault(name = TestingObjects.testVault.name)
                        drawerFunctionality(toOpen = false)
                    }
                )
                holdFor(
                    taskName = "Sort, search and filter",
                    estimatedTime = 13_500,
                    action = {
                        sortPasswordList()
                        search()
                        filterUsingVault()
                    }
                )
                holdFor(
                    taskName = "Auto-lock and unlock app",
                    estimatedTime = 17_000,
                    action = {
                        lockApp()
                        unlockApp(passwordToUse = MasterPasswords.OLD_PASSWORD)
                    }
                )
                holdFor(
                    taskName = "turn on switches and logout",
                    estimatedTime = 15_500,
                    action = {
                        turnOnSwitchesAndLogout()
                    }
                )
                holdFor(
                    taskName = "select account and login",
                    estimatedTime = 21_500,
                    action = {
                        selectGoogleAccount()
                        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)
                    }
                )
                holdFor(
                    taskName = "Change to new password",
                    estimatedTime = 25_000,
                    action = { changeToNewPassword() }
                )
                holdFor(
                    taskName = "Enter master key",
                    estimatedTime = 12_000,
                    action = { enterMasterKey(masterPassword = MasterPasswords.NEW_PASSWORD) }
                )
                holdFor(
                    taskName = "Reset user",
                    estimatedTime = 21_000,
                    action = { resetUser() }
                )
            }
        )
    }

    private suspend fun holdFor(
        taskName: String,
        estimatedTime: Long,
        action: () -> Unit
    ) = withContext(Dispatchers.IO) {
        fun Long.millisPadded(): String {
            return "${this.toString().padStart(length = 6, padChar = ' ')} ms"
        }

        val holder = async { delay(timeMillis = estimatedTime) }
        val startTime = System.currentTimeMillis()
        action()
        val totalTime = System.currentTimeMillis() - startTime
        val diffPercentage = ((estimatedTime - totalTime).toFloat() / estimatedTime.toFloat()) * 100
        ("Lap time for " +
                taskName.padEnd(length = 40, padChar = '-') +
                " | Actual = ${totalTime.millisPadded()}" +
                " | Expected = ${estimatedTime.millisPadded()}" +
                " | Difference = ${(estimatedTime - totalTime).millisPadded()}" +
                (if (diffPercentage > 16.0) " | Check for high diff"
                else if (diffPercentage < 8.0) " | check for low diff"
                else "")).let { msg ->
            if (totalTime < estimatedTime) {
                Log.d(TAG, msg)
            } else {
                Log.e(TAG, msg)
            }
        }
        holder.await()
    }
}

fun main() {
    fun printRow(
        title: String,
        lapTimeEntries: List<Long>?,
        padChar: Char = ' ',
        vararg columns: String,
    ) {
        println(
            "${title.padEnd(length = 40, padChar = padChar)} | " +
                    columns.joinToString(
                        separator = " | ",
                        transform = { it.padStart(length = 9, padChar = padChar) },
                    ) +
                    " || " +
                    lapTimeEntries.let { lt ->
                        lt?.joinToString(
                            separator = ", ",
                            transform = { it.toString().padStart(length = 5, padChar = ' ') }
                        ) ?: "Timeline"
                    }
        )
    }

    printRow(
        title = "Lap Time",
        lapTimeEntries = null,
        padChar = '-',
        columns = arrayOf("Average", "Lowest", "Highest", "Diff")
    )

    /** dark_blue_green | dark_monochrome | light_blue_green | light_yellow_pink */
    val timeline = """
launch app and login to home             | 23585 ms | 23110 ms | 23081 ms | 23408 ms 
Create vault                             | 12606 ms | 11977 ms | 11964 ms | 12312 ms 
Update vault                             | 12187 ms | 12551 ms | 13581 ms | 11935 ms 
Create password                          | 23318 ms | 23620 ms | 22706 ms | 25090 ms 
Update password                          | 16525 ms | 16838 ms | 15852 ms | 17192 ms 
View and delete password                 | 19508 ms | 18796 ms | 18947 ms | 19276 ms 
Delete vault                             | 11047 ms | 11398 ms | 10406 ms | 11706 ms 
Sort, search and filter                  | 10905 ms | 11223 ms | 10279 ms | 10975 ms 
Auto-lock and unlock app                 | 15276 ms | 15730 ms | 14648 ms | 15002 ms 
turn on switches and logout              | 12347 ms | 12666 ms | 12741 ms | 12069 ms 
select account and login                 | 18942 ms | 19470 ms | 19475 ms | 19735 ms 
Change to new password                   | 22956 ms | 22330 ms | 22395 ms | 22778 ms 
Enter master key                         | 10534 ms |  9882 ms | 10038 ms | 10375 ms 
Reset user                               | 15688 ms | 17988 ms | 16174 ms | 16417 ms 
"""
    timeline.trimIndent()
        .split('\n')
        .forEach { entry ->
            val splitStr = entry.split('|').map { it.trim() }
            val timeValues = splitStr.drop(1).map { it.dropLast(n = 3).toLong() }.sorted()

            val average = timeValues.let { tv ->
                var total = 0L
                tv.forEach { total += it }
                return@let (total / tv.size)
            }

            fun Long.millisTransformed(): String {
                return this.toString().padStart(length = 5, padChar = ' ')
            }

            val lowest = timeValues.first()
            val highest = timeValues.last()
            printRow(
                title = splitStr.first().trim(),
                lapTimeEntries = timeValues,
                columns = arrayOf(
                    average.millisTransformed(),
                    lowest.millisTransformed(),
                    highest.millisTransformed(),
                    (highest - lowest).millisTransformed()
                )
            )
        }
}