package easter.egg.passmark.data.models

data class User(
    val userId: String? = null,
    val createdAt: String,
    val personalCheckKey: String
)