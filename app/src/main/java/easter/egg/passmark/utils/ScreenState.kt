package easter.egg.passmark.utils

sealed class ScreenState<T> {
    class Loading<T> : ScreenState<T>()
    class Loaded<T>(val result: T) : ScreenState<T>()
    sealed class ApiError<T> : ScreenState<T>() {
        class NetworkError<T> : ApiError<T>()
        class SomethingWentWrong<T> : ApiError<T>()
    }
}