package easter.egg.passmark.data.models.content.password

import easter.egg.passmark.utils.security.PasswordCryptographyHandler

/** to be used to display stuff and only to be stored in memory */
data class Password(
    val localId: Int?,
    val cloudId: Int?,
    val vaultId: Int? = null,
    val data: PasswordData,
//    val userId: String,

    val created: Long,
    val lastUsed: Long,
    val lastModified: Long,
    val usedCount: Int
) {
    fun toPasswordCapsule(
        passwordCryptographyHandler: PasswordCryptographyHandler,
    ): PasswordCapsule {
        return PasswordCapsule(
            localId = localId,
            cloudId = cloudId,
            vaultId = vaultId,
            data = passwordCryptographyHandler.encryptPasswordData(passwordData = data),
            created = created,
            lastUsed = lastUsed,
            lastModified = lastModified,
            usedCount = usedCount,
//            userId = userId
        )
    }
}

/** used for sorting for passwords */
enum class PasswordSortingOptions {
    NAME, USAGE, LAST_USED, CREATED;

    fun getMenuDisplayText(): String = when (this) {
        NAME -> "Name (asc)"
        USAGE -> "Usage"
        LAST_USED -> "Last Used"
        CREATED -> "Created time"
    }
}