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
            TestVault(name = SOCIAL_MEDIA_ROUTINE, iconIndex = 3),
            TestVault(name = WORK_ROUTINE, iconIndex = 2),
            TestVault(name = FINANCE_ROUTINE, iconIndex = 4),
        )
    }
}