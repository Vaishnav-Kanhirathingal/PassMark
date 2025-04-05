package easter.egg.passmark.utils.extensions

fun String?.nullIfBlank(): String? = this?.takeUnless { it.isBlank() }