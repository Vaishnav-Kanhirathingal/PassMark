package easter.egg.passmark.utils.accessibility.auth

import easter.egg.passmark.utils.accessibility.Describable

enum class LoginDescribable(override val desc: String) : Describable {
    GOOGLE_LOGIN_BUTTON(desc = "Google Login")
}