package easter.egg.passmark.data.models.content

import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//------------------------------------------------------------------------------classes used with UI
/** to be used to display stuff and only to be stored in memory */
data class Password(
    val id: Int? = null,
    val vaultId: Int? = null,
    val data: PasswordData,

    val created: Long,
    val lastUsed: Long,
    val lastModified: Long
) {
    fun toPasswordCapsule(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ): PasswordCapsule = PasswordCapsule(
        id = id,
        vaultId = vaultId,
        data = passwordCryptographyHandler.encryptPasswordData(passwordData = data),
        created = created,
        lastUsed = lastUsed,
        lastModified = lastModified,
    )
}

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

    fun getShortName(): String = this.title
        .split(" ")
        .joinToString(separator = "", transform = { it.first().uppercase() })
        .take(n = 2)

    fun getFavicon(): String? = website?.let { "https://${it}/favicon.ico" }
}

//---------------------------------------------------------------------------------encrypted classes
/** attack safe data with encrypted password data to be stored remotely / on-storage
 * @param data is an encrypted json of [PasswordData]
 */
@Serializable
data class PasswordCapsule(
    @SerialName(value = "id") val id: Int? = null,
    @SerialName(value = "vault_id") val vaultId: Int?,
    @SerialName(value = "data") val data: String,
    @SerialName(value = "created") val created: Long,
    @SerialName(value = "last_used") val lastUsed: Long,
    @SerialName(value = "last_modified") val lastModified: Long
) {
    fun toPassword(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) = Password(
        id = id,
        vaultId = vaultId,
        data = passwordCryptographyHandler.decryptPasswordData(passwordData = this.data),
        created = created,
        lastUsed = lastUsed,
        lastModified = lastModified,
    )
}
