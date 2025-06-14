package easter.egg.passmark.utils.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PassMarkConfig {
    // TODO: re-config for post testing
    const val TIME_OUT = 2000L // TODO: remove in production

    /** pass a task lambda whose result has to be delayed */
    suspend fun <T> holdForDelay(task: suspend () -> T): T = withContext(context = Dispatchers.IO) {
        val holder = async { delay(TIME_OUT) }
        val result = task()
        holder.await()
        return@withContext result
    }
}