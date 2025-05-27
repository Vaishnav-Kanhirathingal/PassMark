package easter.egg.passmark.data.models.content.password

import easter.egg.passmark.data.models.content.password.sensitive.PasswordHistory

// TODO: rename to sensitive components
// TODO: add password history
/** sensitive content inside the password data */
data class PasswordData(
    val title: String,
    val email: String?,
    val userName: String?,
    val password: String,
    val passwordHistory: List<PasswordHistory>,
    val website: String?,
    val notes: String?,
    val useFingerPrint: Boolean,
) {
    companion object {
        val testData = PasswordData(
            title = "Google",
            email = "someone@gmail.com",
            userName = "some_user",
            password = "Some password",
            website = "www.somewebsite.com",
            useFingerPrint = true,
            notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc sodales " +
                    "velit in nisi ornare posuere. Donec nec lectus nisi. Praesent quis " +
                    "tristique nisi. Aenean ac pulvinar nisl. Nunc non luctus dolor.",
            passwordHistory = listOf()
        )
    }

    fun getSubTitle(): String? =
        listOf(email, userName, website).firstOrNull { !it.isNullOrBlank() }

    fun getShortName(): String = this.title.trim()
        .split(" ")
        .joinToString(separator = "", transform = { it.first().uppercase() })
        .take(n = 2)

    fun getFavicon(): String? = website
        ?.takeUnless { it.isBlank() }
        ?.let { "https://${it}/favicon.ico" }
}