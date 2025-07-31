package easter.egg.passmark.data.models.password

import easter.egg.passmark.data.models.password.sensitive.PasswordHistory
import easter.egg.passmark.data.models.password.sensitive.SensitiveContent
import easter.egg.passmark.utils.security.PasswordCryptographyHandler

/** to be used to display stuff and only to be stored in memory */
data class PasswordData(
    val localId: Int?,
    val cloudId: Int?,
    val vaultId: Int? = null,
    val data: SensitiveContent,
    val created: Long,
    val lastUsed: Long,
    val lastModified: Long,
    val usedCount: Int
) {
    companion object {
        private val now = System.currentTimeMillis()
        val testPasswordData = PasswordData(
            localId = null,
            cloudId = 0,
            data = SensitiveContent.testData,
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
            data = passwordCryptographyHandler.encryptSensitiveContent(sensitiveContent = data),
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
        NAME -> "Name"
        USAGE -> "Usage"
        LAST_USED -> "Last Used"
        CREATED -> "Created time"
    }
}