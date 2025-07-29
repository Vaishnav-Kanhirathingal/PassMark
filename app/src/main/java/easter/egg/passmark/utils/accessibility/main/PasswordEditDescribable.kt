package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

enum class PasswordEditDescribable(override val desc: String) : Describable {
    DISMISS(desc = "Cancel"),
    SELECT_VAULT_BUTTON(desc = "Select Vault"),
    SELECT_VAULT_DIALOG_CHOOSE_VAULT_TITLE(desc = "Select Vault"),
    SAVE_BUTTON(desc = "Save Password"),
    TITLE_TEXT_FIELD(desc = "Title"),
    EMAIL_TEXT_FIELD(desc = "Email"),
    USER_NAME_TEXT_FIELD(desc = "User name"),
    PASSWORD_TEXT_FIELD(desc = "Password to save"),
    WEBSITE_TEXT_FIELD(desc = "service's website URL"),
    NOTES_TEXT_FIELD(desc = "Notes"),
    USE_FINGERPRINT_SWITCH(desc = "Use fingerprint"),
    KEEP_LOCAL_SWITCH(desc = "Save locally");

    companion object {
        fun getVaultTestTag(vaultName: String): Describable = object : Describable {
            override val desc: String
                get() = "Vault $vaultName"
        }
    }
}