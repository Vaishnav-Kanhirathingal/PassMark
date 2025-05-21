package easter.egg.passmark.data

data class TestVault(
    val name: String,
    val iconIndex: Int
) {
    companion object {
        const val SOCIAL_MEDIA_ROUTINE = "Social"
        const val WORK_ROUTINE = "Work"
        const val FINANCE_ROUTINE = "Finance"
        val vaultTestList = listOf(
            TestVault(name = "Social Media", iconIndex = 3),
            TestVault(name = "Finance", iconIndex = 2),
            TestVault(name = "Work", iconIndex = 4),
        )
    }
}