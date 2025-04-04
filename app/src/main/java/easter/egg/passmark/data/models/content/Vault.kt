package easter.egg.passmark.data.models.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("icon_choice") val iconChoice: Int
)

/** types of passwords -
 * devices
 * websites
 * application
 * banking
 * unimportant
 */