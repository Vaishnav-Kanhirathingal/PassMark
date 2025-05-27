package easter.egg.passmark.data

data class TestVault(
    val name: String,
    val iconIndex: Int
) {
    companion object {
        const val SOCIAL_MEDIA_VAULT = "Social"
        const val WORK_VAULT = "Work"
        const val FINANCE_VAULT = "Finance"
        val vaultTestList = listOf(
            TestVault(name = SOCIAL_MEDIA_VAULT, iconIndex = 3),
            TestVault(name = WORK_VAULT, iconIndex = 2),
            TestVault(name = FINANCE_VAULT, iconIndex = 4),
        )
    }
}