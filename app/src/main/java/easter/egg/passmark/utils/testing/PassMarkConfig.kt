package easter.egg.passmark.utils.testing

import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PassMarkConfig {
    // TODO: re-config for production release
    const val TIME_OUT = 2000L
    const val AUTO_LOCK_ENABLED = true
    private const val USE_PASSWORD_KEYBOARD_TYPE = false

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