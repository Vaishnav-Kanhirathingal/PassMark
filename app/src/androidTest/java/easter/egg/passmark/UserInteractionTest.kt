package easter.egg.passmark

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import easter.egg.passmark.utils.testing.TestTags
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserInteractionTest {

    @Test
    fun launchApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val packageName = "easter.egg.passmark"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent!!.apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) })
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000)
        Thread.sleep(3_000)
    }

    @Test
    fun loginToHome() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.findObject(By.desc(TestTags.Login.GOOGLE_BUTTON.name)).click()
        Thread.sleep(1_500)
        device.findObject(By.text("vaishnav.kanhira@gmail.com")).click()
        Thread.sleep(8_000)
        device.findObject(By.desc(TestTags.CreateMasterKey.TEXT_FIELD.name)).click()
        Thread.sleep(1_000)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_1)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_2)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_3)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_4)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_5)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_6)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_7)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_8)
        device.pressKeyCode(android.view.KeyEvent.KEYCODE_9)
        Thread.sleep(3_000)
        device.findObject(By.desc(TestTags.CreateMasterKey.CONFIRM_BUTTON.name)).click()
        Thread.sleep(5_000)
    }

    @Test
    fun fullScript() {
        launchApp()
        loginToHome()
    }
}