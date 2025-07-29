package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

enum class AutoLockDescribable(override val desc: String) : Describable {
    PASSWORD_TEXT_FIELD(desc = "enter your master password"),
    VISIBILITY_BUTTON(desc = "visibility toggle"),
    FINGERPRINT_BUTTON(desc = "fingerprint verification"),
    CONFIRM_BUTTON(desc = "confirm password")

}