package easter.egg.passmark.utils.accessibility.screens

import easter.egg.passmark.utils.accessibility.Describable

// TODO: add others too
enum class PasswordViewDescribable(override val desc: String) : Describable {
    EMAIL_COPY_BUTTON(desc ="Copy email" ),
    USER_NAME_COPY_BUTTON(desc = "Copy user name"),
    PASSWORD_HISTORY_BUTTON(desc = "View password history"),
    PASSWORD_COPY_BUTTON(desc = "Copy password"),
    PASSWORD_FINGERPRINT_VERIFICATION_BUTTON(desc = "Biometric verification before viewing password"),
    WEBSITE_COPY_BUTTON(desc = "Copy website url"),
    DELETE_PASSWORD_BUTTON(desc = "Delete password")
}