package easter.egg.passmark.utils.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object Descriptions {
    fun Modifier.setDescription(describable: Describable): Modifier {
        return this
            .semantics { this.contentDescription = describable.desc }
            .testTag(tag = describable.desc)
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
        VISIBILITY_OFF(desc = "Visibility off"),
        VISIBILITY_ON(desc = "Visibility on"),
        CREATE_BUTTON(desc = "Create new user"),
        CONFIRM_BUTTON(desc = "Verify master key")
    }

    enum class Home(override val desc: String) : Describable {
        CREATE_NEW_PASSWORD(desc = "Create a new password");

        enum class TopBar(override val desc: String) : Describable {
            SORTING(desc = "Sorting options"),
            SEARCH_BUTTON(desc = "Search"),
            SEARCH_TEXT_FIELD(desc = "Search box"),
            BACK_BUTTON(desc = "Back"),
            OPEN_DRAWER_BUTTON(desc = "Open drawer");

            enum class Sorting(override val desc: String) : Describable {
                INCREASING_ORDER(desc = "Increasing"),
                DECREASING_ORDER(desc = "Decreasing"),
                NAME(desc = "Sort by name"),
                USAGE(desc = "Sort by usage"),
                LAST_USED(desc = "Sort by last used"),
                CREATED(desc = "Sort by time");
            }
        }

        companion object {
            fun getPasswordTag(name: String): Describable {
                return object : Describable {
                    override val desc: String get() = "$name password"
                }
            }

            fun getPasswordOptionsTag(name: String): Describable {
                return object : Describable {
                    override val desc: String get() = "$name options"
                }
            }
        }

        enum class PasswordOptionsBottomSheet(override val desc: String) : Describable {
            EDIT_BUTTON(desc = "Edit password"), // TODO: add others
        }

        enum class Drawer {
            TOP_TITLE,
            CREATE_NEW_VAULT_BUTTON,
            SETTINGS;

            enum class VaultDialog {
                TEXT_FIELD,
                CONFIRM_BUTTON,
                DELETE_BUTTON;

                companion object {
                    fun getIconTag(index: Int) = "icon_$index"
                }
            }
        }

    }
}