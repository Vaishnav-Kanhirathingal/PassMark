package easter.egg.passmark.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException

/** the screen state class which maintains the state of an api call. set the type `T` to `Unit` if
 * no return is required
 * */
sealed class ScreenState<T> {
    /** this is the state before any api call is made */
    class PreCall<T> : ScreenState<T>()

    class Loading<T> : ScreenState<T>()

    /** loaded state, contains the result of the output */
    class Loaded<T>(val result: T) : ScreenState<T>()

    sealed class ApiError<T>(
        val generalToastMessage: String
    ) : ScreenState<T>() {
        companion object {
            fun <T> fromException(e: Exception): ApiError<T> {
                return when (e) {
                    is HttpRequestTimeoutException, is HttpRequestException -> NetworkError()
                    else -> SomethingWentWrong()
                }
            }
        }

        private val TAG = this::class.simpleName

        var errorHasBeenDisplayed: Boolean = false
            private set

        @Deprecated(
            message = "use ",
            replaceWith = ReplaceWith("this.manageToastActions()")
        )
        fun setErrorHasBeenDisplayed() {
            this.errorHasBeenDisplayed = true
        }

        /** this function manages whether we have to display a toast. It internally handles one time
         * execution of a toast
         * @param context used to display a toast
         */
        fun manageToastActions(context: Context) {
            if (!this.errorHasBeenDisplayed) {
                this.errorHasBeenDisplayed = true
                Toast.makeText(
                    context,
                    this.generalToastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.d(TAG, "avoided displaying error multiple times")
            }
        }

        class NetworkError<T> : ApiError<T>(
            generalToastMessage = "No internet connection. Please check your network."
        )

        class SomethingWentWrong<T>(alternateToastMessage: String? = null) : ApiError<T>(
            generalToastMessage = alternateToastMessage ?: "Something went wrong. Please try again."
        )
    }

    val isLoading get() = (this is Loading)
}