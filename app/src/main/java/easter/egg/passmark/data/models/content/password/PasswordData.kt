package easter.egg.passmark.data.models.content.password


/** sensitive content inside the password data */
data class PasswordData(
    val title: String,
    val email: String?,
    val userName: String?,
    val password: String,
    val website: String?,
    val notes: String?,
    val useFingerPrint: Boolean,
    val saveToLocalOnly: Boolean
) {
    fun getSubTitle(): String? =
        listOf(email, userName, website).firstOrNull { !it.isNullOrBlank() }

    fun getShortName(): String = this.title.trim()
        .split(" ")
        .joinToString(separator = "", transform = { it.first().uppercase() })
        .take(n = 2)

    fun getFavicon(): String? = website?.let { "https://${it}/favicon.ico" }
}