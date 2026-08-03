package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.taskvault.domain.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val timestamp: Long,
    val dueDate: Long?,
    val remindMe: Long?,
    val priority: String
)

fun TodoEntity.toDomainModel(): Todo {
    return Todo(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        timestamp = timestamp,
        dueDate = dueDate,
        remindMe = remindMe,
        priority = priority
    )
}

fun Todo.toEntityModel(): TodoEntity {
    return TodoEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        timestamp = timestamp,
        dueDate = dueDate,
        remindMe = remindMe,
        priority = priority
    )
}
