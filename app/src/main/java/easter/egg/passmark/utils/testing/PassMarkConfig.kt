package easter.egg.passmark.utils.testing

import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PassMarkConfig {
    // TODO: add a line about these configs, secure activity, keyboard type, auto-lock
    // TODO: re-config for production release
    /** This is the time by which api call results are delayed. Purpose is to allow animations to
     * have enough time to show or beautification of the app */
    const val TIME_OUT = 2_000L

    /** Should the app lock itself if it is pushed to recent apps or minimised */
    const val AUTO_LOCK_ENABLED = true

    /** This is used to change keyboard type for passwords. Makes it possible to record screen */
    private const val USE_PASSWORD_KEYBOARD_TYPE = false

    /** Secure activity disallows users to peak on the screen content from the recent apps tab */
    const val USE_SECURE_ACTIVITY = false

    /** pass a task lambda whose result has to be delayed */
    suspend fun <T> holdForDelay(task: suspend () -> T): T = withContext(context = Dispatchers.IO) {
        val holder = async { delay(TIME_OUT) }
        val result = task()
        holder.await()
        return@withContext result
    }

    /** this is the keyboard type to be used for passwords */
    fun getKeyboardTypeForPasswords(): KeyboardType {
        return if (USE_PASSWORD_KEYBOARD_TYPE) KeyboardType.Password
        else KeyboardType.Text
    }
}