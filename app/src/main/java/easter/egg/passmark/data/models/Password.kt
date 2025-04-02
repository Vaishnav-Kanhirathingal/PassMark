package easter.egg.passmark.data.models

/** to be used to display stuff and only to be stored in memory */
data class Password(
    val id: Int? = null,
    val vaultId: Int?,
    val data: PasswordData,
    val lastUsed: Long
) {
    fun toPasswordCapsule(key: String): PasswordCapsule {
        TODO("encrypt to string and create capsule")
    }
}

/** sensitive content inside the password data */
data class PasswordData(
    val title: String,
    val email: String,
    val userName: String,
    val password: String,
    val website: String,
    val notes: String,
    val useFingerPrint: Boolean,
    val saveToLocalOnly: Boolean
)

/** attack safe data with encrypted password data to be stored remotely / on-storage */
data class PasswordCapsule(
    val id: Int? = null,
    val vaultId: Int?,
    val data: String,
    val lastUsed: Long?
)