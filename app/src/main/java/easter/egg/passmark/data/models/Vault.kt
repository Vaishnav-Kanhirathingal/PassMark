package easter.egg.passmark.data.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SensorOccupied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("icon_choice") val iconChoice: Int
) {
    companion object {
        val iconList = listOf(
            Icons.Default.Public,
            Icons.Default.AccountBalance,
            Icons.Default.Smartphone,
            Icons.Default.Group,
            Icons.Default.Apartment,
            Icons.Default.Handshake,
            Icons.Default.ShoppingCart,
            Icons.Default.Settings,
            Icons.Default.Lock,
            Icons.Default.Star,
            Icons.Default.SensorOccupied,
            Icons.Default.Games
        )

        const val VAULT_NAME_FOR_ALL_ITEMS = "All items"
        val allItemsIcon = iconList.first()

        fun Vault?.getIcon(): ImageVector {
            return this?.iconChoice?.let { iconList.getOrNull(index = it) } ?: iconList.first()
        }
    }
}

/** types of passwords -
 * devices
 * websites
 * application
 * banking
 * unimportant
 */
@Composable
@Preview(
    widthDp = 100,
    heightDp = 400,
    showBackground = true
)
private fun IconListPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Vault.iconList.forEach {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        }
    )
}