package easter.egg.passmark.data.models.content.password

import easter.egg.passmark.data.models.content.password.sensitive.PasswordHistory
import easter.egg.passmark.utils.security.PasswordCryptographyHandler

// TODO: rename to PasswordData
/** to be used to display stuff and only to be stored in memory */
data class Password(
    val localId: Int?,
    val cloudId: Int?,
    val vaultId: Int? = null,
    val data: PasswordData,
    val created: Long,
    val lastUsed: Long,
    val lastModified: Long,
    val usedCount: Int
) {
    companion object {
        private val now = System.currentTimeMillis()
        val testPassword = Password(
            localId = null,
            cloudId = 0,
            data = PasswordData.testData,
            created = now,
            lastUsed = now,
            lastModified = now,
            usedCount = 0,
        )
    }

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
        )
    }

    fun currentPasswordAsPasswordHistory(): PasswordHistory {
        return PasswordHistory(
            password = this.data.password,
            discardedOn = System.currentTimeMillis()
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