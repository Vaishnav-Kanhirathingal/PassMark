package easter.egg.passmark.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName(value = "user_id") val userId: String? = null,
    @SerialName(value = "created_at") val createdAt: String? = null,
    @SerialName(value = "password_puzzle_encrypted") val passwordPuzzleEncrypted: String,
    @SerialName(value = "encryption_key_initialization_vector") val encryptionKeyInitializationVector: String
) {
    companion object {
        const val PUZZLE_SOLUTION =
            "gGR/5pKTUvgxRbtjNA38r00ArR4n6HVwtWKG3M/pakx+XNlZLtXphlInW9AwhmsI0edA6V+UJsdDQamREftnGS9CfZGVN22g4OdBz8vgV9slWjSE+Dte3EM11p0KVdN8MSUD5A"
    }
}