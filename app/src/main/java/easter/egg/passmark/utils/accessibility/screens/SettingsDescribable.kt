package easter.egg.passmark.utils.accessibility.screens

import easter.egg.passmark.utils.accessibility.Describable

enum class SettingsDescribable(override val desc: String) : Describable {
    LOCAL_STORAGE_SWITCH(desc = "Choose local storage by default"),
    FINGERPRINT_AUTHENTICATION_SWITCH(desc = "Require fingerprint authentication by default"),
    CHANGE_PASSWORD_BUTTON(desc = "Change password"),
    LOG_OUT(desc = "Log out"),
    RESET_ACCOUNT_BUTTON(desc = "Reset your account")
}