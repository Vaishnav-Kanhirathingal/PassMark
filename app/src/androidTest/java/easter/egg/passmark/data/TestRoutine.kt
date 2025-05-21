package easter.egg.passmark.data

data class TestRoutine(
    val name: String,
    val iconIndex: Int
) {
    companion object {
        val routineTestList = listOf(
            TestRoutine(name = "Social Media", iconIndex = 3),
            TestRoutine(name = "Finance", iconIndex = 2),
            TestRoutine(name = "Work", iconIndex = 4),
        )
    }
}