package easter.egg.passmark.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName(value = "user_id") val userId: String? = null,
    @SerialName(value = "created_at") val createdAt: String,
    @SerialName(value = "personal_check_key") val personalCheckKey: String
)