package easter.egg.passmark

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import easter.egg.passmark.data.TestPasswordData
import easter.egg.passmark.data.TestVault
import easter.egg.passmark.utils.testing.TestTags
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserInteractionTest {
    companion object {
        const val SMALL_ANIMATION_DELAY = 1_000L

        const val NAVIGATION_DELAY = 3_000L
        const val SINGLE_CALL_LOADING_DELAY = 3_000 + TestTags.TIME_OUT
        const val INITIAL_LOADING_SCREEN_DELAY = 3_000 + (2 * TestTags.TIME_OUT)
    }

    private fun findObject(testTag: String): UiObject2 {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        return device.findObject(By.desc(testTag))
    }

    @Test
    fun launchApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val packageName = "easter.egg.passmark"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent!!.apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) })
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000)
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)
    }

    fun type(txt: String) {
        InstrumentationRegistry.getInstrumentation().sendStringSync(txt)
    }

    private fun loginToHome() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        findObject(TestTags.Login.GOOGLE_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        device.findObject(By.text("vaishnav.kanhira@gmail.com")).click()
        Thread.sleep(INITIAL_LOADING_SCREEN_DELAY)
        findObject(testTag = TestTags.CreateMasterKey.VISIBILITY_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(TestTags.CreateMasterKey.TEXT_FIELD.name).let {
            it.click()
            Thread.sleep(SMALL_ANIMATION_DELAY)
            type(txt = "123456789")
        }
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(TestTags.CreateMasterKey.CONFIRM_BUTTON.name).click()
        Thread.sleep(INITIAL_LOADING_SCREEN_DELAY)
    }

    /** call with an open home drawer and completes with an open drawer (is repeatable) */
    private fun createVault(testVault: TestVault) {
        findObject(TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)
        findObject(TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name).let {
            it.click()
            Thread.sleep(SMALL_ANIMATION_DELAY)
            type(testVault.name)
        }
        findObject(TestTags.Home.Drawer.VaultDialog.getIconTag(index = testVault.iconIndex)).click()
        findObject(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name).click()
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)
    }

    /** called from home screen, exits to home screen (is repeatable) */
    private fun createPassword(testPasswordData: TestPasswordData) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        findObject(TestTags.Home.CREATE_NEW_PASSWORD_BUTTON.name).click()
        Thread.sleep(NAVIGATION_DELAY)
        testPasswordData.vault?.let {
            this.findObject(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name).click()
            Thread.sleep(SMALL_ANIMATION_DELAY)
            this.findObject(testTag = TestTags.EditPassword.ChooseVault.getVaultTestTag(vaultName = it))
                .click()
//            device.findObject(By.text(it)).click()
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
        Thread.sleep(SINGLE_CALL_LOADING_DELAY)
    }

    @Test
    fun fullScript() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        launchApp()
        loginToHome()

        //------------------------------------------------------------------------------------vaults
        device.findObject(By.desc(TestTags.Home.OPEN_DRAWER_BUTTON.name)).click()
        Thread.sleep(SMALL_ANIMATION_DELAY)

//        TestVault.vaultTestList.forEach(action = this::createVault)
//        createVault(testVault = TestVault.vaultTestList[0])

        device.click(1080, 1440)
        Thread.sleep(SMALL_ANIMATION_DELAY)

        //---------------------------------------------------------------------------------passwords
//        TestPasswordData.testList.forEach(action = this::createPassword)
        this.createPassword(testPasswordData = TestPasswordData.testList[1])
    }
}