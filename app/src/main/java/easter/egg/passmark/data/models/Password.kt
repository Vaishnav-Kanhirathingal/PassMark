package easter.egg.passmark.data.models

data class Password(
    val id: Int? = null,
    val vaultId: Int?,
    val title: String,
    val email: String,
    val userName: String,
    val password: String,
    val website: String,
    val notes: String,
    val useFingerPrint: Boolean,
    val saveToLocalOnly: Boolean
)