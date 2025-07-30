package easter.egg.passmark.utils.accessibility.auth

import easter.egg.passmark.utils.accessibility.Describable

enum class MasterKeyDescribable(override val desc: String) : Describable {
    MASTER_KEY_TEXT_FIELD(desc = "master password"),
    VISIBILITY(desc = "Visibility"),
    CREATE_BUTTON(desc = "Create new user"),
    CONFIRM_BUTTON(desc = "Verify master key")
}