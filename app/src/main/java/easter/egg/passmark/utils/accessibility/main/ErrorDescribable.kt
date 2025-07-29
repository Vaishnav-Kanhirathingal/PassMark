package easter.egg.passmark.utils.accessibility.main

import easter.egg.passmark.utils.accessibility.Describable

enum class ErrorDescribable(override val desc: String) : Describable {
    RETRY_BUTTON(desc = "retry")
}