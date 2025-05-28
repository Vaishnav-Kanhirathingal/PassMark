package easter.egg.passmark.data.models.password

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** attack safe data with encrypted password data to be stored remotely / on-storage
 * @param data is an encrypted json of [PasswordData]
 */
@Serializable
@Entity(tableName = "local_password_capsules")
data class PasswordCapsule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(Keys.LOCAL_ID_KEY) @SerialName(value = Keys.LOCAL_ID_KEY) val localId: Int? = null,

    @Ignore @SerialName(value = Keys.SUPABASE_ID_KEY) val cloudId: Int? = null,

    @ColumnInfo(Keys.VAULT_ID_KEY) @SerialName(value = Keys.VAULT_ID_KEY) val vaultId: Int?,
    @ColumnInfo(Keys.DATA_KEY) @SerialName(value = Keys.DATA_KEY) val data: String,
    @ColumnInfo(Keys.CREATED_KEY) @SerialName(value = Keys.CREATED_KEY) val created: Long,
    @ColumnInfo(Keys.LAST_USED_KEY) @SerialName(value = Keys.LAST_USED_KEY) val lastUsed: Long,
    @ColumnInfo(Keys.LAST_MODIFIED_KEY) @SerialName(value = Keys.LAST_MODIFIED_KEY) val lastModified: Long,
    @ColumnInfo(Keys.USED_COUNT_KEY) @SerialName(value = Keys.USED_COUNT_KEY) val usedCount: Int,
//    @ColumnInfo(Keys.USER_ID) @SerialName(value = Keys.USER_ID) val userId: String
) {
    constructor(
        localId: Int?,
        vaultId: Int?,
        data: String,
        created: Long,
        lastUsed: Long,
        lastModified: Long,
        usedCount: Int,
//        userId: String
    ) : this(
        cloudId = null,
        localId = localId,
        vaultId = vaultId,
        data = data,
        created = created,
        lastUsed = lastUsed,
        lastModified = lastModified,
        usedCount = usedCount,
//        userId = userId
    )

    fun toPassword(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) = Password(
        localId = localId,
        cloudId = cloudId,
        vaultId = vaultId,
        data = passwordCryptographyHandler.decryptPasswordData(passwordData = this.data),
        created = created,
        lastUsed = lastUsed,
        lastModified = lastModified,
        usedCount = usedCount,
//        userId = userId
    )

    companion object {
        object Keys {
            const val LOCAL_ID_KEY = "local_id"
            const val SUPABASE_ID_KEY = "id"
            const val VAULT_ID_KEY = "vault_id"
            const val DATA_KEY = "data"
            const val CREATED_KEY = "created"
            const val LAST_USED_KEY = "last_used"
            const val LAST_MODIFIED_KEY = "last_modified"
            const val USED_COUNT_KEY = "used_count"
//            const val USER_ID = "user_id"
        }
    }
}