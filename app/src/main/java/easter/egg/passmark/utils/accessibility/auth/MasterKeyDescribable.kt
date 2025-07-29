package easter.egg.passmark.utils.accessibility.auth

import easter.egg.passmark.utils.accessibility.Describable

enum class MasterKeyDescribable(override val desc: String) : Describable {
    MASTER_KEY_TEXT_FIELD(desc = "master key"),
    VISIBILITY_OFF(desc = "Visibility off"),
    VISIBILITY_ON(desc = "Visibility on"),
    CREATE_BUTTON(desc = "Create new user"),
    CONFIRM_BUTTON(desc = "Verify master key")
}