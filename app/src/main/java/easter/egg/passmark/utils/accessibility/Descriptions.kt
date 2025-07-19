package easter.egg.passmark.utils.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object Descriptions {
    fun Modifier.setDescription(describable: Describable): Modifier {
        return this.semantics { this.contentDescription = describable.desc }
    }

    interface Describable {
        val desc: String
    }

    enum class Login(override val desc: String) : Describable {
        GOOGLE_LOGIN_BUTTON(desc = "Google Login")
    }

    enum class MasterKey(override val desc: String) : Describable {
        CREATE_MASTER_KEY_TEXT_FIELD(desc = "Create new master key"),
        ENTER_MASTER_KEY_TEXT_FIELD(desc = "Enter you master key"),
        CREATE_BUTTON(desc = "create"),
        CONFIRM_BUTTON(desc = "confirm")
    }
}