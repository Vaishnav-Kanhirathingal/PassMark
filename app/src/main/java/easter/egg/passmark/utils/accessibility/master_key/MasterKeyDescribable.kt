package easter.egg.passmark.utils.accessibility.master_key

import easter.egg.passmark.utils.accessibility.Describable

enum class MasterKeyDescribable(override val desc: String) : Describable {
    CREATE_MASTER_KEY_TEXT_FIELD(desc = "Create new master key"),
    ENTER_MASTER_KEY_TEXT_FIELD(desc = "Enter you master key"),
    VISIBILITY_OFF(desc = "Visibility off"),
    VISIBILITY_ON(desc = "Visibility on"),
    CREATE_BUTTON(desc = "Create new user"),
    CONFIRM_BUTTON(desc = "Verify master key")
}