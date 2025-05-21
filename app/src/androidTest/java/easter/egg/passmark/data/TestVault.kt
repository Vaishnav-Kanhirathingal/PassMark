package easter.egg.passmark.data

data class TestVault(
    val name: String,
    val iconIndex: Int
) {
    companion object {
        val routineTestList = listOf(
            TestVault(name = "Social Media", iconIndex = 3),
            TestVault(name = "Finance", iconIndex = 2),
            TestVault(name = "Work", iconIndex = 4),
        )
    }
}