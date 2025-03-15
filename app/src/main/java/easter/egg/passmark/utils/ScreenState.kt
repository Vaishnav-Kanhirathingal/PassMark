package easter.egg.passmark.utils

/** the screen state class which maintains the state of an api call. set the type `T` to `Unit` if no
 * return is required
 * */
sealed class ScreenState<T> {
    /** this is the state before any api call is made */
    class PreCall<T> : ScreenState<T>()

    class Loading<T> : ScreenState<T>()

    /** loaded state, contains the result of the output */
    class Loaded<T>(val result: T) : ScreenState<T>()

    sealed class ApiError<T> : ScreenState<T>() {
        var errorHasBeenDisplayed: Boolean = false
            private set

        fun setErrorHasBeenDisplayed() {
            this.errorHasBeenDisplayed = true
        }

        class NetworkError<T> : ApiError<T>()
        class SomethingWentWrong<T> : ApiError<T>()
    }
}