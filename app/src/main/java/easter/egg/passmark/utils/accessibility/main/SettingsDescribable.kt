package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

enum class SettingsDescribable(override val desc: String) : Describable {
    LOCAL_STORAGE_SWITCH(desc = "Choose local storage by default"),
    FINGERPRINT_AUTHENTICATION_SWITCH(desc = "Require fingerprint authentication by default"),
    CHANGE_PASSWORD_BUTTON(desc = "Change password"),
    LOG_OUT_BUTTON(desc = "Log out"),
    RESET_ACCOUNT_BUTTON(desc = "Reset your account");

    enum class ResetUserAccountDialog(override val desc: String) : Describable {
        RESET_USER_BUTTON(desc = "Reset user"),
        CANCEL_BUTTON(desc = "Cancel")
    }
}