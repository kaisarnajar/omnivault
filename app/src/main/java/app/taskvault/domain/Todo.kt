package app.taskvault.domain

data class Todo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: Long = 0L
)
