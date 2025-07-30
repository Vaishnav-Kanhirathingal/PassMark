package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

// TODO: add others too
enum class PasswordViewDescribable(override val desc: String) : Describable {
    BACK_BUTTON("exit view password screen"),
    EDIT_BUTTON("edit current password"),
    EMAIL_COPY_BUTTON(desc = "Copy email"),
    USER_NAME_COPY_BUTTON(desc = "Copy user name"),
    PASSWORD_HISTORY_BUTTON(desc = "View password history"),
    PASSWORD_COPY_BUTTON(desc = "Copy password"),
    PASSWORD_FINGERPRINT_VERIFICATION_BUTTON(desc = "Biometric verification before viewing password"),
    WEBSITE_COPY_BUTTON(desc = "Copy website url"),
    DELETE_PASSWORD_BUTTON(desc = "Delete password");

    companion object {
        fun getVaultDescribable(vaultName: String): Describable {
            return object : Describable {
                override val desc: String get() = "vault $vaultName"
            }
        }
    }

    enum class DeletePasswordDialog(override val desc: String) : Describable {
        DELETE_BUTTON(desc = "Delete password"),
        CANCEL_BUTTON(desc = "Cancel")
    }
}