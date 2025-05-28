package easter.egg.passmark

import android.content.Intent
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
import easter.egg.passmark.utils.testing.TestTags
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.absoluteValue

@RunWith(AndroidJUnit4::class)
class ActionAutomatorTest {
    companion object {
        const val SMALL_ANIMATION_DELAY = 1_000L

        const val NAVIGATION_DELAY = 3_000L
        const val SINGLE_CALL_LOADING_DELAY = 3_000 + TestTags.TIME_OUT
        const val INITIAL_LOADING_SCREEN_DELAY = 3_000 + (2 * TestTags.TIME_OUT)
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
            userName = "Easter123",
            password = getTestingPassword(index = 0),
            website = "nvidia.com",
            note = "3rd gen",
            useFingerprint = true,
            useLocalStorage = true
        )
    }

    //-------------------------------------------------------------------------------------recording

    @Before
    fun startRecording() {
        val themeSelected = "Cyan" // TODO: update every time
        val command = "screenrecord /sdcard/TestRecordings/${themeSelected}FullFlow.mp4"
        InstrumentationRegistry.getInstrumentation().uiAutomation.let {
            it.executeShellCommand("mkdir -p /sdcard/TestRecordings")
            it.executeShellCommand(command)
        }
    }

    @After
    fun stopRecording() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pkill -l2 screenrecord")
    }

    //---------------------------------------------------------------------------------------utility
    private fun findObject(testTag: String): UiObject2 {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.desc(testTag)), 3_000)
        return device.findObject(By.desc(testTag))
    }

    private fun type(txt: String) {
        InstrumentationRegistry.getInstrumentation().sendStringSync(txt)
    }

    private fun type(
        testTag: String,
        text: String
    ) {
        findObject(testTag = testTag).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        type(txt = text)
    }

    //---------------------------------------------------------------------------------------actions
    private fun launchApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val packageName = "easter.egg.passmark"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent!!.apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) })
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000)
        Thread.sleep(NAVIGATION_DELAY)
    }

    private fun selectGoogleAccount() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(TestTags.Login.GOOGLE_BUTTON.name).click()
        Thread.sleep(NAVIGATION_DELAY)
        device.findObject(By.text("vaishnav.kanhira@gmail.com")).click()
        Thread.sleep(INITIAL_LOADING_SCREEN_DELAY)

    }

    private fun enterMasterKey(masterPassword: String) {
        findObject(testTag = TestTags.CreateMasterKey.VISIBILITY_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        type(
            testTag = TestTags.CreateMasterKey.TEXT_FIELD.name,
            text = masterPassword
        )
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(TestTags.CreateMasterKey.CONFIRM_BUTTON.name).click()
        Thread.sleep(INITIAL_LOADING_SCREEN_DELAY)

    }

    /** call with an open home drawer and completes with an open drawer (is repeatable) */
    private fun createVault(testVault: TestVault) {
        findObject(TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        type(
            testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name,
            text = testVault.name
        )
        findObject(TestTags.Home.Drawer.VaultDialog.getIconTag(index = testVault.iconIndex)).click()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name).click()
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)
    }

    /** called from home screen, exits to home screen (is repeatable) */
    private fun createPassword(testPasswordData: TestPasswordData) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.wait(Until.hasObject(By.desc(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name)), 2_000)
        findObject(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name).click()
        Thread.sleep(NAVIGATION_DELAY)

        testPasswordData.vault?.let {
            device.wait(
                Until.hasObject(By.desc(TestTags.EditPassword.SELECT_VAULT_BUTTON.name)),
                2_000
            )
            this.findObject(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name).click()
            Thread.sleep(SMALL_ANIMATION_DELAY)
            device.findObject(By.text(it)).click()
            Thread.sleep(SMALL_ANIMATION_DELAY)
        }

        fun textHandler(
            testTag: String,
            text: String?
        ) {
            text?.let {
                findObject(testTag = testTag).click()
                Thread.sleep(SMALL_ANIMATION_DELAY)
                type(txt = it)
            }
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
        Thread.sleep(8_000)
    }

    private fun viewAndDeletePassword(passwordName: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(testTag = TestTags.Home.getPasswordTag(name = passwordName)).click()
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)

        device.wait(Until.hasObject(By.desc(TestTags.ViewPassword.FINGERPRINT_BUTTON.name)), 3_000)
        findObject(testTag = TestTags.ViewPassword.FINGERPRINT_BUTTON.name).click()
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)

        UiScrollable(UiSelector().scrollable(true)).scrollToEnd(1)

        device.wait(Until.hasObject(By.desc(TestTags.ViewPassword.DELETE_BUTTON.name)), 3_000)
        findObject(testTag = TestTags.ViewPassword.DELETE_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(testTag = TestTags.ConfirmationDialog.POSITIVE_BUTTON.name).click()
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)
    }

    /** call from home screen without open drawer */
    private fun resetUser() {
        drawerFunctionality(toOpen = true)
        findObject(testTag = TestTags.Home.Drawer.SETTINGS.name).click()
        Thread.sleep(NAVIGATION_DELAY)
        findObject(testTag = TestTags.Settings.RESET_ACCOUNT_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(testTag = TestTags.ConfirmationDialog.POSITIVE_BUTTON.name).click()
        Thread.sleep(4_000 + SINGLE_CALL_LOADING_DELAY)
    }

    /** call from home screen without open drawer. exits at master password screen with a request
     * to enter password
     */
    private fun changePassword(
        oldPassword: String,
        newPassword: String
    ) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        findObject(testTag = TestTags.Home.Drawer.SETTINGS.name).click()
        Thread.sleep(NAVIGATION_DELAY)
        findObject(testTag = TestTags.Settings.CHANGE_PASSWORD_BUTTON.name).click()
        Thread.sleep(NAVIGATION_DELAY)

        device.wait(
            Until.hasObject(By.desc(TestTags.ChangePassword.ORIGINAL_PASSWORD_TEXT_FIELD.name)),
            3_000
        )

        type(
            testTag = TestTags.ChangePassword.ORIGINAL_PASSWORD_TEXT_FIELD.name,
            text = oldPassword
        )
        type(
            testTag = TestTags.ChangePassword.NEW_PASSWORD_TEXT_FIELD.name,
            text = newPassword
        )
        type(
            testTag = TestTags.ChangePassword.NEW_PASSWORD_REPEATED_TEXT_FIELD.name,
            text = newPassword
        )
        device.pressBack()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(testTag = TestTags.ChangePassword.CONFIRM_BUTTON.name).click()
        Thread.sleep(5_000 + SINGLE_CALL_LOADING_DELAY)
    }

    /** to be called from home */
    private fun drawerFunctionality(toOpen: Boolean) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (toOpen) {
            findObject(testTag = TestTags.Home.TopBar.OPEN_DRAWER_BUTTON.name).click()
        } else {
            device.click(1080, 1440)
        }
        Thread.sleep(SMALL_ANIMATION_DELAY)
    }

    private fun sortPasswordList() {
        findObject(testTag = TestTags.Home.TopBar.SORTING_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(testTag = TestTags.Home.Sorting.getSortOptionTag(passwordSortingOptions = PasswordSortingOptions.NAME)).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
    }

    private fun search() {
        findObject(testTag = TestTags.Home.TopBar.SEARCH_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        "goo".forEach {
            type(txt = it.toString())
            Thread.sleep(SMALL_ANIMATION_DELAY)
        }

        Thread.sleep(SMALL_ANIMATION_DELAY)

        findObject(testTag = TestTags.Home.TopBar.BACK_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
    }

    private fun filterUsingVault() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        drawerFunctionality(toOpen = true)
        device.wait(Until.hasObject(By.text(TestVault.WORK_VAULT)), 3_000)
        device.findObject(By.text(TestVault.WORK_VAULT)).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
    }

    @Test
    fun fullScript() {
        launchApp()
        selectGoogleAccount()
        enterMasterKey(masterPassword = MasterPasswords.OLD_PASSWORD)

        drawerFunctionality(toOpen = true)
        createVault(testVault = TestingObjects.testVault)
        drawerFunctionality(toOpen = false)

        createPassword(testPasswordData = TestingObjects.testPasswordData)
        viewAndDeletePassword(passwordName = TestingObjects.testPasswordData.title)

        sortPasswordList()
        search()
        filterUsingVault()

        changePassword(
            oldPassword = MasterPasswords.OLD_PASSWORD,
            newPassword = MasterPasswords.NEW_PASSWORD
        )
        enterMasterKey(masterPassword = MasterPasswords.NEW_PASSWORD)
        resetUser()
    }
}

/** Scripts to record -
 * login
 * create vault
 * create password
 * view (for both fingerprint and non fingerprint) and delete password
 * sorting
 * searching passwords
 * vault filtering
 * change password
 * reset account
 */