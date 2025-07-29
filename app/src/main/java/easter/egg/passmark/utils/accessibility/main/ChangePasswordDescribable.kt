package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

enum class ChangePasswordDescribable(override val desc: String) : Describable {
    ENTER_CURRENT_PASSWORD(desc = "current password"),
    ENTER_NEW_PASSWORD(desc = "new password"),
    REPEAT_NEW_PASSWORD(desc = "repeat new password"),
    CANCEL(desc = "cancel change of password process"),
    CONFIRM(desc = "change password")
}